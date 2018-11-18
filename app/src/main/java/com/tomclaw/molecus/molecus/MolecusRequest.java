package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.core.JsonRequest;
import com.tomclaw.molecus.core.Response;
import com.tomclaw.molecus.util.HttpParamsBuilder;
import com.tomclaw.molecus.util.HttpUtil;

/**
 * Created by solkin on 03.11.15.
 */
public abstract class MolecusRequest<A extends Response> extends JsonRequest<A> {

    public static final String API_BASE_URL = DevHelper.fixUrls("http://molecus.ru/api/");

    @Override
    protected String getHttpRequestType() {
        return HttpUtil.GET;
    }

    @Override
    protected String getUrl() {
        return API_BASE_URL + getApiName();
    }

    protected abstract String getApiName();

    @Override
    protected final HttpParamsBuilder getParams() {
        HttpParamsBuilder builder = new HttpParamsBuilder();
        if (isUserBased()) {
            builder.appendParam("access_token", getUserHolder().getUser().getAccessToken());
        }
        appendParams(builder);
        return builder;
    }

    protected abstract void appendParams(HttpParamsBuilder builder);

    @Override
    public boolean isUserBased() {
        return false;
    }
}
