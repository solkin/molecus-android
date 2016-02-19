package com.tomclaw.molecus.core;

/**
 * Created by solkin on 16/02/16.
 */
public interface RequestCallback<R extends Response> {

    void onSuccess(R response);

    void onFailed(Request.RequestException ex);

    void onUnauthorized();

    void onRetry();

    void onCancelled();
}
