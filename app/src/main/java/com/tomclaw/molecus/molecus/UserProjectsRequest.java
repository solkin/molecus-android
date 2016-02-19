package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.util.HttpParamsBuilder;

/**
 * Created by solkin on 20/02/16.
 */
public class UserProjectsRequest extends ProjectsRequest {

    private final String user;

    public UserProjectsRequest(String user, int offset, int count) {
        super(ProjectsType.list, offset, count);
        this.user = user;
    }

    @Override
    protected void appendProjectsParams(HttpParamsBuilder builder) {
        builder.appendParam("user", user);
    }
}
