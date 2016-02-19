package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.util.HttpParamsBuilder;

/**
 * Created by solkin on 20/02/16.
 */
public class VotingRequest extends ProjectsRequest {

    public VotingRequest(int offset, int count) {
        super(ProjectsType.voting, offset, count);
    }

    @Override
    protected void appendProjectsParams(HttpParamsBuilder builder) {
    }
}
