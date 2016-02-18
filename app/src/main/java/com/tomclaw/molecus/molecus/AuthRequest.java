package com.tomclaw.molecus.molecus;

import android.text.TextUtils;

import com.tomclaw.molecus.util.HttpParamsBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by solkin on 03.11.15.
 */
public class AuthRequest extends MolecusRequest<AuthResponse> {

    private String login;
    private String password;
    private String refreshToken;

    public AuthRequest() {
    }

    public AuthRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public AuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    protected String getApiName() {
        return "auth";
    }

    @Override
    protected void appendParams(HttpParamsBuilder builder) {
        if (TextUtils.isEmpty(refreshToken)) {
            builder.appendParam("grant_type", "password")
                    .appendParam("login", login)
                    .appendParam("password", password);
        } else {
            builder.appendParam("grant_type", "refresh_token")
                    .appendParam("refresh_token", refreshToken);
        }
    }

    @Override
    public boolean isUserBased() {
        return false;
    }

    @Override
    protected AuthResponse parseJson(JSONObject response) throws JSONException, RequestPendingException {
        int status = response.getInt("status");
        if (status == 200) {
            String accessToken = response.getString("access_token");
            String refreshToken = response.getString("refresh_token");
            long expiresIn = response.getLong("expires_in");
            return new AuthResponse(status, accessToken, refreshToken, expiresIn);
        }
        return new AuthResponse(status);
    }
}
