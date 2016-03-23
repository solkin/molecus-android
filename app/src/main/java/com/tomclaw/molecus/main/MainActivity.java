package com.tomclaw.molecus.main;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tomclaw.molecus.R;
import com.tomclaw.molecus.core.Request;
import com.tomclaw.molecus.core.RequestCallback;
import com.tomclaw.molecus.core.RequestExecutor;
import com.tomclaw.molecus.core.User;
import com.tomclaw.molecus.core.UserHolder;
import com.tomclaw.molecus.main.adapters.EndlessAdapter;
import com.tomclaw.molecus.main.adapters.EndlessProjectsAdapter;
import com.tomclaw.molecus.main.controllers.ProjectCache;
import com.tomclaw.molecus.main.controllers.UserInfoCache;
import com.tomclaw.molecus.molecus.AllProjectsRequest;
import com.tomclaw.molecus.molecus.ProjectsRequest;
import com.tomclaw.molecus.molecus.ProjectsResponse;
import com.tomclaw.molecus.molecus.SceneRequest;
import com.tomclaw.molecus.molecus.UserProjectsRequest;
import com.tomclaw.molecus.molecus.dto.Project;
import com.tomclaw.molecus.molecus.dto.UserInfo;
import com.tomclaw.molecus.util.Logger;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.concurrent.Future;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int BLOCK_PROJECTS_COUNT = 50;
    @ViewById
    Toolbar toolbar;

    @ViewById
    DrawerLayout drawerLayout;

    @ViewById
    NavigationView navView;

    @ViewById
    RecyclerView list;

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    ImageView userAvatar;

    TextView userNick;

    TextView userLogin;

    @Bean
    UserHolder userHolder;

    @Bean
    RequestExecutor requestExecutor;

    @Bean
    UserInfoCache userInfoCache;

    @Bean
    ProjectCache projectCache;

    EndlessProjectsAdapter adapter;

    ProjectsRequest request;
    Future<?> future;
    private boolean endless;

    private ActiveTab activeTab;

    @AfterViews
    void init() {
        if (!userHolder.getUser().isAuthorized()) {
            LoginActivity_.intent(this).start();
            finish();
            return;
        }

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: clear list and reload from start.
                refreshProjects();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(false);
        adapter = new EndlessProjectsAdapter(this, new EndlessAdapter.EndlessAdapterListener() {
            @Override
            public void onLoadMoreItems(int offset) {
                Logger.log("load more items: " + offset);
                requestProjects(request.getOffset() + request.getCount());
            }
        });
        adapter.setMoreItemsAvailable(false);
        list.setAdapter(adapter);

        navView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navView.getMenu().getItem(0));

        View header = navView.getHeaderView(0);
        userAvatar = (ImageView) header.findViewById(R.id.user_avatar);
        userNick = (TextView) header.findViewById(R.id.user_nick);
        userLogin = (TextView) header.findViewById(R.id.user_login);

        updateUser();
    }

    @SupposeUiThread
    void updateUser() {
        User user = userHolder.getUser();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(user.getAvatar(), userAvatar,
                ImageOptions.getAvatarOptions());
        userNick.setText(user.getNick());
        userLogin.setText(user.getLogin());
    }

    void requestProjects(ProjectsRequest request, boolean endless) {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        this.request = request;
        this.endless = endless;
        refreshProjects();
    }

    @UiThread(delay = 2500L)
    void refreshProjectsWithDelay() {
        refreshProjects();
    }

    @UiThread
    void refreshProjects() {
        if (request != null) {
            final ProjectsRequest currentRequest = request;
            future = requestExecutor.execute(currentRequest, new RequestCallback<ProjectsResponse>() {
                @Override
                public void onSuccess(ProjectsResponse response) {
                    if (currentRequest != request) {
                        return;
                    }
                    List<Project> projects = response.getProjects();
                    projectCache.saveProjects(projects, new ProjectCache.ProjectsCallback() {
                        @Override
                        public void onProjects(List<Project> projects) {
                            boolean moreItems = !projects.isEmpty() && endless;
                            adapter.setMoreItemsAvailable(moreItems);
                            boolean append = currentRequest.getOffset() > 0 && endless;
                            updateProjects(projects, append);
                        }
                    });
                }

                @Override
                public void onFailed(Request.RequestException ex) {
                    refreshProjectsWithDelay();
                }

                @Override
                public void onUnauthorized() {
                    // TODO: show authorization activity if running.
                }

                @Override
                public void onRetry() {
                    refreshProjects();
                }

                @Override
                public void onCancelled() {
                    // Nothing to do at all.
                }
            });
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @UiThread
    void updateProjects(List<Project> projects, boolean append) {
        if (append) {
            adapter.appendItems(projects);
        } else {
            adapter.setItems(projects);
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Click
    void createProject(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OptionsItem
    void homeSelected() {
        finish();
    }

    @OptionsItem
    boolean actionSettings() {
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        adapter.setMoreItemsAvailable(false);
        switch (id) {
            case R.id.nav_scene:
                activeTab = ActiveTab.Scene;
                setTitle(R.string.scene);
                projectCache.getProjectsRandom(SceneRequest.SCENE_PROJECTS_COUNT, new ProjectCache.ProjectsCallback() {
                    @Override
                    public void onProjects(List<Project> projects) {
                        updateProjects(projects, false);
                        requestProjects(0);
                    }
                });
                break;
            case R.id.nav_all_projects:
                activeTab = ActiveTab.AllProjects;
                setTitle(R.string.all_projects);
                projectCache.getProjectsByLimit(50, new ProjectCache.ProjectsCallback() {
                    @Override
                    public void onProjects(List<Project> projects) {
                        updateProjects(projects, false);
                        requestProjects(0);
                    }
                });
                break;
            case R.id.nav_my_projects:
                activeTab = ActiveTab.MyProjects;
                setTitle(R.string.my_projects);
                userInfoCache.getUserInfo(userHolder.getUser().getNick(),
                        new UserInfoCache.UserInfoCallback() {
                            @Override
                            public void onUserInfo(UserInfo userInfo) {
                                projectCache.getProjectsByUserInfo(userInfo.getId(), new ProjectCache.ProjectsCallback() {
                                    @Override
                                    public void onProjects(List<Project> projects) {
                                        updateProjects(projects, false);
                                        updateFromServer();
                                    }
                                });
                            }

                            @Override
                            public void onNotFound() {
                                updateFromServer();
                            }

                            void updateFromServer() {
                                requestProjects(0);
                            }
                        });
                break;
            case R.id.nav_feed:
                break;
            case R.id.nav_subscribers:
                SubscribersActivity_.intent(this).start();
                break;
            case R.id.nav_dialogs:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void requestProjects(int offset) {
        switch(activeTab) {
            case Scene:
                requestProjects(new SceneRequest(), false);
                break;
            case AllProjects:
                requestProjects(new AllProjectsRequest(offset, BLOCK_PROJECTS_COUNT), true);
                break;
            case MyProjects:
                requestProjects(new UserProjectsRequest(userHolder.getUser().getNick(), offset, BLOCK_PROJECTS_COUNT), true);
                break;
        }
    }

    public enum ActiveTab {
        Scene,
        AllProjects,
        MyProjects
    }
}
