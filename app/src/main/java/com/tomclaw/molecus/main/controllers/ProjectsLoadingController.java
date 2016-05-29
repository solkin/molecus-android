package com.tomclaw.molecus.main.controllers;

import android.os.Bundle;

import com.tomclaw.molecus.core.GlobalProvider;
import com.tomclaw.molecus.core.Request;
import com.tomclaw.molecus.core.RequestCallback;
import com.tomclaw.molecus.core.RequestExecutor;
import com.tomclaw.molecus.core.Settings;
import com.tomclaw.molecus.molecus.AllProjectsRequest;
import com.tomclaw.molecus.molecus.ProjectsRequest;
import com.tomclaw.molecus.molecus.ProjectsResponse;
import com.tomclaw.molecus.molecus.SceneRequest;
import com.tomclaw.molecus.molecus.UserProjectsRequest;
import com.tomclaw.molecus.molecus.dto.Project;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.concurrent.Future;

import static com.tomclaw.molecus.main.Beans.contentResolver;

/**
 * Created by solkin on 28.05.16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ProjectsLoadingController {

    @Bean
    RequestExecutor requestExecutor;

    private ProjectsRequest request;
    private Future<?> future;

    private LoadingCallback loadingCallback;

    private RequestCallback<ProjectsRequest, ProjectsResponse> requestCallback =
            new RequestCallback<ProjectsRequest, ProjectsResponse>() {
                @Override
                public void onSuccess(ProjectsRequest request, ProjectsResponse response) {
                    ArrayList<Project> projects = new ArrayList<>(response.getProjects());
                    Bundle extras = new Bundle();
                    extras.putSerializable(GlobalProvider.KEY_PROJECTS, projects);
                    contentResolver().call(Settings.PROJECTS_RESOLVER_URI,
                            GlobalProvider.METHOD_PROJECTS_MERGE, null, extras);
                    onLoaded(request);
                    reset(request);
                }

                @Override
                public void onFailed(ProjectsRequest request, Request.RequestException ex) {
                    reset(request);
                    requestWithDelay(request);
                }

                @Override
                public void onUnauthorized(ProjectsRequest request) {
                    loadingCallback.onUnauthorized();
                    reset(request);
                }

                @Override
                public void onRetry(ProjectsRequest request) {
                    reset(request);
                    request(request);
                }

                @Override
                public void onCancelled(ProjectsRequest request) {
                    reset(request);
                }
    };

    public void setLoadingCallback(LoadingCallback loadingCallback) {
        this.loadingCallback = loadingCallback;
    }

    public ProjectsRequest getActiveRequest() {
        if (future != null && !future.isDone()) {
            return request;
        }
        return null;
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void requestScene() {
        request(new SceneRequest());
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void requestAllProjects(int offset, int count) {
        request(new AllProjectsRequest(offset, count));
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void requestUserProjects(String user, int offset, int count) {
        request(new UserProjectsRequest(user, offset, count));
    }

    @SupposeUiThread
    void request(ProjectsRequest projectsRequest) {
        Class<?> requestClass = projectsRequest.getClass();
        boolean isRequest = false;
        if (requestClass.isInstance(request)) {
            if (future == null || future.isDone()) {
                isRequest = true;
            }
        } else {
            if (future != null) {
                future.cancel(true);
            }
            isRequest = true;
        }
        if (isRequest) {
            request = projectsRequest;
            future = requestExecutor.execute(request, requestCallback);
        }
    }

    @UiThread(delay = 2500L)
    void requestWithDelay(ProjectsRequest request) {
        request(request);
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void reset(ProjectsRequest request) {
        if (this.request == request) {
            this.request = null;
            future = null;
        }
    }

    private void onLoaded(ProjectsRequest request) {
        if (this.request == request) {
            loadingCallback.onLoaded();
        }
    }

    public interface LoadingCallback {

        void onLoaded();
        void onUnauthorized();
    }
}
