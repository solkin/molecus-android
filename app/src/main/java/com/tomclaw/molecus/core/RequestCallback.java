package com.tomclaw.molecus.core;

/**
 * Created by solkin on 16/02/16.
 */
public interface RequestCallback<R extends Request, S extends Response> {

    void onSuccess(R request, S response);

    void onFailed(R request, Request.RequestException ex);

    void onUnauthorized(R request);

    void onRetry(R request);

    void onCancelled(R request);
}
