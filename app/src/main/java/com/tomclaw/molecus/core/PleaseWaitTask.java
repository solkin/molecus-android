package com.tomclaw.molecus.core;

import android.app.ProgressDialog;
import android.content.Context;

import com.tomclaw.molecus.R;

import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA.
 * User: Solkin
 * Date: 09.11.13
 * Time: 14:19
 */
public abstract class PleaseWaitTask<A extends Context> extends WeakObjectTask<A> {

    private WeakReference<ProgressDialog> weakProgressDialog;

    public PleaseWaitTask(A context) {
        super(context);
    }

    @Override
    public boolean isPreExecuteRequired() {
        return true;
    }

    @Override
    public void onPreExecuteMain() {
        Context context = getWeakObject();
        if (context != null) {
            ProgressDialog progressDialog = ProgressDialog.show(context, null, context.getString(getWaitStringId()));
            weakProgressDialog = new WeakReference<>(progressDialog);
        }
    }

    @Override
    public void onPostExecuteMain() {
        ProgressDialog progressDialog = weakProgressDialog.get();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public int getWaitStringId() {
        return R.string.please_wait;
    }
}
