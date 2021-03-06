package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.util.HttpParamsBuilder;

/**
 * Created by Solkin on 04.11.2015.
 */
public class SceneRequest extends ProjectsRequest {

    public static final int SCENE_PROJECTS_COUNT = 10;

    public SceneRequest() {
        super(ProjectsType.scene, 0, SCENE_PROJECTS_COUNT);
    }

    @Override
    protected void appendProjectsParams(HttpParamsBuilder builder) {
    }
}
