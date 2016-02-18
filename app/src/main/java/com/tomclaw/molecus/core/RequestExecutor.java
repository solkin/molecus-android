package com.tomclaw.molecus.core;

import android.content.Context;

import com.tomclaw.molecus.molecus.AuthRequest;
import com.tomclaw.molecus.molecus.AuthResponse;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by solkin on 16/02/16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class RequestExecutor {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final ExecutorService delayedExecutor = Executors.newSingleThreadExecutor();

    @RootContext
    Context context;

    @Bean
    UserHolder userHolder;

    public <S extends Response, R extends Request<S>> void execute(
            R request, RequestCallback<S> callback) {
        executor.submit(new RequestWrapper<>(context, userHolder, request, callback));
    }

    public <S extends Response, R extends Request<S>> void execute(
            final R request, final RequestCallback<S> callback, final long delay) {
        delayedExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                execute(request, callback);
            }
        });
    }

    public static class RequestWrapper<S extends Response, R extends Request<S>> implements Runnable {

        private final UserHolder userHolder;
        private WeakReference<Context> weakContext;
        private R request;
        private RequestCallback<S> callback;

        public RequestWrapper(Context context, UserHolder userHolder, R request, RequestCallback<S> callback) {
            this.weakContext = new WeakReference<>(context);
            this.userHolder = userHolder;
            this.request = request;
            this.callback = callback;
        }

        @Override
        public void run() {
            Context context = weakContext.get();
            if (context != null) {
                try {
                    if (request.isUserBased() && !userHolder.getUser().isAuthorized()) {
                        if (!refreshToken(userHolder, context)) {
                            callback.onUnauthorized();
                            return;
                        }
                    }
                    S response = request.onRequest(userHolder, context);
                    if (response.isSuccess()) {
                        callback.onSuccess(response);
                    } else if (response.isUnauthorized()) {
                        if (refreshToken(userHolder, context)) {
                            callback.onRetry();
                        } else {
                            callback.onUnauthorized();
                        }
                    }
                } catch (Request.RequestException ex) {
                    callback.onFailed(ex);
                }
            }
        }

        static boolean refreshToken(UserHolder userHolder, Context context) {
            AuthRequest request = new AuthRequest(userHolder.getUser().getRefreshToken());
            try {
                AuthResponse response = request.onRequest(userHolder, context);
                userHolder.getUser().setData(userHolder.getUser().getLogin(),
                        response.getAccessToken(),
                        response.getRefreshToken(),
                        response.getExpiresIn());
                return true;
            } catch (Request.RequestException ex) {
                userHolder.getUser().setUnauthorized();
                return false;
            }
        }
    }

}
