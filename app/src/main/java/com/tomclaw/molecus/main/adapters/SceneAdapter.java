package com.tomclaw.molecus.main.adapters;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.tomclaw.molecus.molecus.SceneRequest;
import com.tomclaw.molecus.util.QueryBuilder;

/**
 * Created by solkin on 28.05.16.
 */
public class SceneAdapter extends ProjectsAdapter {

    private static final int ADAPTER_ID = 0x01;

    public SceneAdapter(Context context, LoaderManager loaderManager) {
        super(context, loaderManager, ADAPTER_ID);
    }

    @Override
    public QueryBuilder configureQuery(QueryBuilder queryBuilder) {
        return queryBuilder.orderRandom().limit(SceneRequest.SCENE_PROJECTS_COUNT);
    }
}
