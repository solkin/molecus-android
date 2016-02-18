package com.tomclaw.molecus.core;

import android.database.ContentObserver;
import android.text.TextUtils;
import com.tomclaw.molecus.main.Molecus;
import com.tomclaw.molecus.util.Logger;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.tomclaw.molecus.main.Beans.userHolder;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 21/11/15
 * Time: 11:20 AM
 */
public class RequestDispatcher {

    private static final long PENDING_REQUEST_DELAY = 4000;
    /**
     * Variables
     */
    private int requestType;
    private DispatcherRunnable runnable;
    private ThreadPoolExecutor executor;

    private volatile String executingRequestTag;

    public RequestDispatcher(int requestType) {
        // Request type.
        this.requestType = requestType;
        // Initializing executor and observer.
        initExecutor();
        runnable = new DispatcherRunnable();
    }

    private void initExecutor() {
        executor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(3));
    }

    /**
     * Stops task with specified tag.
     *
     * @param tag - tag of the task needs to be stopped.
     */
    public boolean stopRequest(String tag) {
        // First of all, check that task is executing or in queue.
        if (TextUtils.equals(tag, executingRequestTag)) {
            // Task is executing this moment.
            // Interrupt thread as faster as it can be!
            // Task will receive interrupt exception.
            executor.shutdownNow();
            initExecutor();
            notifyQueue();
            return true;
        } else {
            // Huh... Task is only in scheduled queue.
            // We can simply mark is as delayed "REQUEST_LATER".
            List<Request> requestList = Request.find(Request.class, "requestTag = ?", String.valueOf(tag));
            for (Request request : requestList) {
                request.setRequestState(Request.REQUEST_LATER);
            }
            Request.saveInTx(requestList);
            return false;
        }
    }

    private class DispatcherRunnable implements Runnable {

        @Override
        public void run() {
            List<Request> requests = Request.find(Request.class, "requestType = ?, requestState != ?", String.valueOf(requestType), String.valueOf(Request.REQUEST_LATER));
            if (!requests.isEmpty()) {
                dispatch(requests);
            }
        }

        @SuppressWarnings("unchecked")
        private void dispatch(List<Request> requestList) {
            // Yeah, we are ready.
            log("Dispatching requests.");
            for (Request request : requestList) {
                if (request.getRequestState() == Request.REQUEST_LATER) {
                    continue;
                }
                log("Request received: " + request.toString());
                Response response;
                try {
                    // Checking for user is authorized.
                    if (!userHolder().getUser().isAuthorized()) {
                        // User is not authorized!
                        // TODO: Need for real implementation.
                        throw new RuntimeException("User is not authorized!");
                    }
                    executingRequestTag = request.getRequestTag();
                    response = request.onRequest(userHolder(), Molecus.getInstance());
                } catch (Request.RequestDeleteException ex) {
                    request.delete();
                    requestList.remove(request);
                } catch (Request.RequestPendingException ex) {
                    break;
                } catch (Request.RequestLaterException ex) {
                    request.setRequestState(Request.REQUEST_LATER);
                    request.save();
                } catch (Throwable ex) {
                    log("Exception while executing request", ex);
                } finally {
                    executingRequestTag = null;
                }
            }
            log("Dispatching completed, pending requests: " + requestList.size());
            if (!requestList.isEmpty()) {
                // Pending guarantee dispatching after delay.
                log("Pending guarantee dispatching after delay");
                try {
                    Thread.sleep(PENDING_REQUEST_DELAY);
                } catch (InterruptedException ignored) {
                }
                notifyQueue();
            }
        }
    }

    private void log(String message) {
        Logger.log("rd[" + requestType + "]: " + message);
    }

    private void log(String message, Throwable exception) {
        Logger.log("rd[" + requestType + "]: " + message, exception);
    }

    public void notifyQueue() {
        try {
            executor.submit(runnable);
            log("Queue notification accepted.");
        } catch (RejectedExecutionException ignored) {
            // All right, this is useless task.
            log("Queue notification received, but we already have notification.");
        }
    }
}
