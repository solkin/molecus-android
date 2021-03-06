package com.tomclaw.molecus.core;

import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created by Solkin on 04.11.2015.
 */
public abstract class Response implements Unobfuscatable {

    private int status;

    public Response(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return status == 200;
    }

    public boolean isUnauthorized() {
        return status == 401;
    }

    public int getStatus() {
        return status;
    }
}
