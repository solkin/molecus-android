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
import com.tomclaw.molecus.core.User;
import com.tomclaw.molecus.core.UserHolder;
import com.tomclaw.molecus.main.adapters.AllProjectsAdapter;
import com.tomclaw.molecus.main.adapters.ProjectsAdapter;
import com.tomclaw.molecus.main.adapters.SceneAdapter;
import com.tomclaw.molecus.main.adapters.UserProjectsAdapter;
import com.tomclaw.molecus.main.controllers.ProjectsLoadingController;
import com.tomclaw.molecus.molecus.ProjectsRequest;
import com.tomclaw.molecus.molecus.dto.Project;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProjectsLoadingController.LoadingCallback {

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
    ProjectsLoadingController loadingController;

    private ProjectsAdapter adapter;

    private ActiveTab activeTab;

    private ProjectsAdapter.OnItemShowListener showListener;

    private int lastProjectOffsetRequest = 0;

    @AfterViews
    void init() {
        if (!userHolder.getUser().isAuthorized()) {
            LoginActivity_.intent(this).start();
            finish();
            return;
        }

        loadingController.setLoadingCallback(this);

        showListener = new ProjectsAdapter.OnItemShowListener() {
            @Override
            public boolean onLastItem(int position, Project project) {
                int offset = position + 1;
                ProjectsRequest request = loadingController.getActiveRequest();
                if (request != null && request.getOffset() == offset) {
                    return true;
                } else if (lastProjectOffsetRequest != offset) {
                    switch (activeTab) {
                        case AllProjects:
                            lastProjectOffsetRequest = offset;
                            loadingController.requestAllProjects(offset, BLOCK_PROJECTS_COUNT);
                            return true;
                        case MyProjects:
                            lastProjectOffsetRequest = offset;
                            loadingController.requestUserProjects(userHolder.getUser().getNick(),
                                    offset, BLOCK_PROJECTS_COUNT);
                            return true;
                    }
                }
                return false;
            }
        };

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestProjects(0);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(false);

        navView.getMenu().getItem(1).setChecked(true);
        onNavigationItemSelected(navView.getMenu().getItem(1));

        View header = navView.getHeaderView(0);
        userAvatar = (ImageView) header.findViewById(R.id.user_avatar);
        userNick = (TextView) header.findViewById(R.id.user_nick);
        userLogin = (TextView) header.findViewById(R.id.user_login);

        updateUser();
    }

    @UiThread
    void requestProjects(int offset) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        switch (activeTab) {
            case Scene:
                loadingController.requestScene();
                break;
            case AllProjects:
                loadingController.requestAllProjects(offset, BLOCK_PROJECTS_COUNT);
                break;
            case MyProjects:
                loadingController.requestUserProjects(userHolder.getUser().getNick(), offset, BLOCK_PROJECTS_COUNT);
                break;
        }
    }

    @Override
    @UiThread
    public void onLoaded() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    @UiThread
    public void onUnauthorized() {
        Snackbar.make(list, "Seems that you unauthorized", Snackbar.LENGTH_LONG).show();
    }

    private void switchAdapter(final ProjectsAdapter projectsAdapter) {
        lastProjectOffsetRequest = 0;
        adapter = projectsAdapter;
        adapter.setShowListener(showListener);
        list.setAdapter(adapter);
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
        switch (id) {
            case R.id.nav_scene:
                activeTab = ActiveTab.Scene;
                setTitle(R.string.scene);
                switchAdapter(new SceneAdapter(this, getSupportLoaderManager()));
                requestProjects(0);
                break;
            case R.id.nav_all_projects:
                activeTab = ActiveTab.AllProjects;
                setTitle(R.string.all_projects);
                switchAdapter(new AllProjectsAdapter(this, getSupportLoaderManager()));
                requestProjects(0);
                break;
            case R.id.nav_my_projects:
                activeTab = ActiveTab.MyProjects;
                setTitle(R.string.my_projects);
                switchAdapter(new UserProjectsAdapter(this, getSupportLoaderManager()));
                requestProjects(0);
                break;
            case R.id.nav_feed:
                break;
            case R.id.nav_subscribers:
                SubscribersActivity_.intent(this).start();
                break;
            case R.id.nav_dialogs:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public enum ActiveTab {
        Scene,
        AllProjects,
        MyProjects
    }
}
