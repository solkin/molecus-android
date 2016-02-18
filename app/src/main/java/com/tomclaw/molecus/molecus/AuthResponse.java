package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.core.Response;

/**
 * Created by Solkin on 18.11.2015.
 */
public class AuthResponse extends Response {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public AuthResponse(int status) {
        super(status);
    }

    public AuthResponse(int status, String accessToken, String refreshToken, long expiresIn) {
        super(status);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
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
}
