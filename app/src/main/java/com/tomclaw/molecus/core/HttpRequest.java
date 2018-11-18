package com.tomclaw.molecus.core;

import android.text.TextUtils;

import com.tomclaw.molecus.util.HttpParamsBuilder;
import com.tomclaw.molecus.util.HttpUtil;
import com.tomclaw.molecus.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 12/5/13
 * Time: 2:09 PM
 */
public abstract class HttpRequest<A extends Response> extends Request<A> {

    private static final String QUE = "?";

    @Override
    public A executeRequest() throws RequestException {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            boolean isGetRequest = getHttpRequestType().equals(HttpUtil.GET);
            url = new URL(isUrlWithParameters() ? getUrlWithParameters() : getUrl());
            urlConnection = (HttpURLConnection) url.openConnection();
            // Executing request.
            InputStream in = isGetRequest ?
                    HttpUtil.executeGet(urlConnection) :
                    HttpUtil.executePost(urlConnection, getBody());
            A result = parseResponse(in);
            // Almost done. Close stream.
            in.close();
            return result;
        } catch (RequestException e) {
            Logger.log("Request exception", e);
            throw e;
        } catch (InterruptedIOException | InterruptedException e) {
            Logger.log("Request interrupted", e);
            throw new RequestCancelledException();
        } catch (Throwable e) {
            Logger.log("Unable to execute request due to exception", e);
            throw new RequestPendingException();
        } finally {
            // Trying to disconnect in any case.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * Returns HTTP request method: GET or POST.
     *
     * @return request method.
     */
    protected abstract String getHttpRequestType();

    /**
     * This method parses String response from server and returns request status.
     *
     * @param httpResponseStream - stream to be parsed.
     * @return int - request status.
     * @throws Throwable
     */
    protected abstract A parseResponse(InputStream httpResponseStream) throws Throwable;

    protected byte[] getBody() throws IOException {
        return HttpUtil.stringToArray(HttpUtil.prepareParameters(getParams()));
    }

    /**
     * Returns request-specific base Url (most of all from WellKnownUrls).
     *
     * @return Request-specific base Url.
     */
    protected abstract String getUrl();

    protected boolean isUrlWithParameters() {
        return getHttpRequestType().equals(HttpUtil.GET);
    }

    /**
     * Returns parameters, must be appended to the Get request.
     *
     * @return List of Get parameters.
     */
    protected abstract HttpParamsBuilder getParams();

    /**
     * Returns url with prepared parameters to perform Get request.
     *
     * @return String - prepared Url.
     * @throws UnsupportedEncodingException
     */
    private String getUrlWithParameters() throws UnsupportedEncodingException {
        // Obtain request-specific url.
        String url = getUrl();
        String parameters = getParams().build();
        Logger.log("API request: ".concat(url).concat(QUE).concat(parameters));
        if (!TextUtils.isEmpty(parameters)) {
            url = url.concat(QUE).concat(parameters);
        }
        return url;
    }
}
