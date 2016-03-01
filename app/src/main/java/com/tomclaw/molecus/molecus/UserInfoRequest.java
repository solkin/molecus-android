package com.tomclaw.molecus.molecus;

import android.text.TextUtils;

import com.tomclaw.molecus.util.GsonSingleton;
import com.tomclaw.molecus.util.HttpParamsBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import static com.tomclaw.molecus.main.Beans.gsonSingleton;

/**
 * Created by Solkin on 03.11.2015.
 */
public class UserInfoRequest extends MolecusRequest<UserInfoResponse> {

    private String user;

    public UserInfoRequest() {
    }

    public UserInfoRequest(String user) {
        this.user = user;
    }

    @Override
    protected String getApiName() {
        return "user_info";
    }

    @Override
    public boolean isUserBased() {
        return true;
    }

    @Override
    protected final void appendParams(HttpParamsBuilder builder) {
        if (!TextUtils.isEmpty(user)) {
            builder.appendParam("user", user);
        }
    }

    @Override
    protected UserInfoResponse parseJson(JSONObject response) throws JSONException, RequestPendingException {
        int status = response.getInt("status");
        if (status == 200) {
            JSONObject userObject = response.getJSONObject("user");
            return gsonSingleton().fromJson(userObject.toString(), UserInfoResponse.class);
        }
        return new UserInfoResponse(status);
    }
}
