package com.tomclaw.molecus.main;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.tomclaw.molecus.R;
import com.tomclaw.molecus.core.Request;
import com.tomclaw.molecus.core.RequestCallback;
import com.tomclaw.molecus.core.RequestExecutor;
import com.tomclaw.molecus.core.Response;
import com.tomclaw.molecus.core.UserHolder;
import com.tomclaw.molecus.main.adapters.ProjectsAdapter;
import com.tomclaw.molecus.molecus.ProjectsResponse;
import com.tomclaw.molecus.molecus.SceneRequest;
import com.tomclaw.molecus.molecus.dto.Project;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.List;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_LOGIN = 0x01;

    @ViewById
    Toolbar toolbar;

    @ViewById
    DrawerLayout drawerLayout;

    @ViewById
    NavigationView navView;

    @ViewById
    RecyclerView list;

    @Bean
    UserHolder userHolder;

    @Bean
    RequestExecutor requestExecutor;

    ProjectsAdapter adapter;

    @AfterInject
    void checkUser() {
        if (!userHolder.getUser().isAuthorized()) {
            LoginActivity_.intent(this).startForResult(REQUEST_CODE_LOGIN);
        }
    }

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(false);
        adapter = new ProjectsAdapter(this, Collections.<Project>emptyList());
        list.setAdapter(adapter);
    }

    @UiThread
    void updateProjects(List<Project> projects) {
        adapter.setProjects(projects);
        adapter.notifyDataSetChanged();
    }

    @Click
    void createProject(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        final SceneRequest request = new SceneRequest();
        requestExecutor.execute(request, new RequestCallback<ProjectsResponse>() {
            @Override
            public void onSuccess(ProjectsResponse response) {
                updateProjects(response.getProjects());
            }

            @Override
            public void onFailed(Request.RequestException ex) {
                requestExecutor.execute(request, this, 2500);
            }

            @Override
            public void onUnauthorized() {
                // TODO: show authorization activity if running.
            }

            @Override
            public void onRetry() {
                requestExecutor.execute(request, this);
            }
        });
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

    @OnActivityResult(REQUEST_CODE_LOGIN)
    void onResult(int resultCode) {
        if (resultCode != RESULT_OK) {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
