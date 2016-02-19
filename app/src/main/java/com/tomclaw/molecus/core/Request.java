package com.tomclaw.molecus.core;

import android.content.Context;
import com.orm.SugarRecord;
import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 6/9/13
 * Time: 7:25 PM
 */
public abstract class Request<A extends Response> extends SugarRecord implements Unobfuscatable {

    /**
     * Request types
     */
    public static final int REQUEST_TYPE_SHORT = 0x00;
    public static final int REQUEST_TYPE_DOWNLOAD = 0x01;
    public static final int REQUEST_TYPE_UPLOAD = 0x02;

    /**
     * Request state flags
     */
    public static final int REQUEST_PENDING = 0x00;
    public static final int REQUEST_SENT = 0x01;
    public static final int REQUEST_LATER = 0x02;
    public static final int REQUEST_DELETE = 0xff;

    private int requestType;
    private int requestState;
    private String requestTag;

    private transient UserHolder userHolder;
    private transient Context context;

    public Request() {
    }

    public Request(int requestType, int requestState, String requestTag) {
        this.requestType = requestType;
        this.requestState = requestState;
        this.requestTag = requestTag;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getRequestState() {
        return requestState;
    }

    public void setRequestState(int requestState) {
        this.requestState = requestState;
    }

    public String getRequestTag() {
        return requestTag;
    }

    public void setRequestTag(String requestTag) {
        this.requestTag = requestTag;
    }

    public UserHolder getUserHolder() {
        return userHolder;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Builds outgoing request and sends it over the network.
     *
     * @return int - status we must setup to this request
     */
    public final A onRequest(UserHolder userHolder, Context context)
            throws RequestException {
        this.userHolder = userHolder;
        this.context = context;
        return executeRequest();
    }

    public abstract A executeRequest() throws RequestException;

    public abstract boolean isUserBased();

    public static abstract class RequestException extends Exception {
    }

    public static class RequestPendingException extends RequestException {
    }

    public static class RequestLaterException extends RequestException {
    }

    public static class RequestDeleteException extends RequestException {
    }

    public static class RequestCancelledException extends RequestException {
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestTag='" + requestTag + '\'' +
                ", requestState=" + requestState +
                ", requestType=" + requestType +
                '}';
    }
}
