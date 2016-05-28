package com.tomclaw.molecus.main.adapters;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.tomclaw.molecus.core.GlobalProvider;
import com.tomclaw.molecus.util.QueryBuilder;

/**
 * Created by solkin on 28.05.16.
 */
public class AllProjectsAdapter extends ProjectsAdapter {

    private static final int ADAPTER_ID = 0x02;

    public AllProjectsAdapter(Context context, LoaderManager loaderManager) {
        super(context, loaderManager, ADAPTER_ID);
    }

    @Override
    public QueryBuilder configureQuery(QueryBuilder queryBuilder) {
        return queryBuilder
                .descending(GlobalProvider.PROJECT_TIME_ADDED).andOrder()
                .descending(GlobalProvider.ROW_AUTO_ID);
    }
}
