package com.tomclaw.molecus.main;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.tomclaw.molecus.R;
import com.tomclaw.molecus.core.Request;
import com.tomclaw.molecus.core.RequestCallback;
import com.tomclaw.molecus.core.RequestExecutor;
import com.tomclaw.molecus.main.adapters.SubscribersAdapter;
import com.tomclaw.molecus.main.controllers.UserInfoCache;
import com.tomclaw.molecus.molecus.SubscribersRequest;
import com.tomclaw.molecus.molecus.SubscribersResponse;
import com.tomclaw.molecus.molecus.dto.UserInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.List;

/**
 * Created by solkin on 03/03/16.
 */
@EActivity(R.layout.subscribers_activity)
@OptionsMenu(R.menu.subscribers)
public class SubscribersActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    RecyclerView list;

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    @Bean
    UserInfoCache userInfoCache;

    @Bean
    RequestExecutor requestExecutor;

    private SubscribersAdapter adapter;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSubscribers();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(false);
        adapter = new SubscribersAdapter(this, Collections.<UserInfo>emptyList());
        list.setAdapter(adapter);

        refreshSubscribers();
    }

    @OptionsItem(android.R.id.home)
    void homeSelected() {
        finish();
    }

    @UiThread(delay = 2500L)
    void refreshSubscribersWithDelay() {
        refreshSubscribers();
    }

    @UiThread
    void refreshSubscribers() {
        swipeRefreshLayout.setRefreshing(true);
        SubscribersRequest subscribersRequest = new SubscribersRequest();
        requestExecutor.execute(subscribersRequest, new RequestCallback<SubscribersResponse>() {
            @Override
            public void onSuccess(SubscribersResponse response) {
                List<UserInfo> userInfoList = response.getUsers();
                userInfoCache.saveUsersInfo(userInfoList, new UserInfoCache.UserInfoSaveCallback() {
                    @Override
                    public void onSaved(List<UserInfo> userInfoList) {
                        updateSubscribers(userInfoList);
                    }
                });
            }

            @Override
            public void onFailed(Request.RequestException ex) {
                refreshSubscribersWithDelay();
            }

            @Override
            public void onUnauthorized() {
                // TODO: show authorization activity if running.
            }

            @Override
            public void onRetry() {
                refreshSubscribers();
            }

            @Override
            public void onCancelled() {
                // Nothing to do at all.
            }
        });
    }

    @UiThread
    void updateSubscribers(List<UserInfo> projects) {
        adapter.setSubscribers(projects);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
