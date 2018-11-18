package com.tomclaw.molecus.core;

import android.text.TextUtils;

import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created by Igor on 07.07.2015.
 */
public class User implements Unobfuscatable {

    private String login;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    private String avatar;
    private String nick;

    public User() {
    }

    public boolean isAuthorized() {
        return !TextUtils.isEmpty(accessToken);
    }

    public String getLogin() {
        return login;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setData(String login, String accessToken, String refreshToken, long expiresIn) {
        this.login = login;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public void setInfo(String avatar, String nick) {
        this.avatar = avatar;
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setUnauthorized() {
        accessToken = null;
    }
}
