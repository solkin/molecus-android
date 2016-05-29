package com.tomclaw.molecus.core;

import com.tomclaw.molecus.util.HttpUtil;
import com.tomclaw.molecus.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Igor on 07.07.2015.
 */
public abstract class JsonRequest<A extends Response> extends HttpRequest<A> {

    @Override
    protected A parseResponse(InputStream httpResponseStream) throws Throwable {
        String responseString = HttpUtil.streamToString(httpResponseStream);
        Logger.log("API response: " + responseString);
        return parseJson(parseResponse(responseString));
    }

    protected JSONObject parseResponse(String responseString) throws JSONException {
        return new JSONObject(responseString);
    }

    protected abstract A parseJson(JSONObject response) throws JSONException, RequestPendingException;
}
