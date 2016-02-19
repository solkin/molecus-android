package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.util.HttpParamsBuilder;

/**
 * Created by solkin on 20/02/16.
 */
public class AllProjectsRequest extends ProjectsRequest {

    public AllProjectsRequest(int offset, int count) {
        super(ProjectsType.list, offset, count);
    }

    @Override
    protected void appendProjectsParams(HttpParamsBuilder builder) {
    }
}
