package com.tomclaw.molecus.molecus;

import com.google.gson.reflect.TypeToken;
import com.tomclaw.molecus.molecus.dto.UserInfo;
import com.tomclaw.molecus.util.HttpParamsBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.tomclaw.molecus.main.Beans.gsonSingleton;

public class SubscribersRequest extends MolecusRequest<SubscribersResponse> {

    public SubscribersRequest() {
    }

    @Override
    protected String getApiName() {
        return "subscribers";
    }

    @Override
    public boolean isUserBased() {
        return true;
    }

    @Override
    protected final void appendParams(HttpParamsBuilder builder) {
    }

    @Override
    protected SubscribersResponse parseJson(JSONObject response) throws JSONException, RequestPendingException {
        int status = response.getInt("status");
        if (status == 200) {
            String usersJson = response.getJSONArray("users").toString();
            Type listType = new TypeToken<ArrayList<UserInfo>>() {}.getType();
            List<UserInfo> users = gsonSingleton().fromJson(usersJson, listType);
            return new SubscribersResponse(status, users);
        }
        return new SubscribersResponse(status);
    }
}
