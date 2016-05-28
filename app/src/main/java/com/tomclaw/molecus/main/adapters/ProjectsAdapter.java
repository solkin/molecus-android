package com.tomclaw.molecus.main.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.tomclaw.molecus.core.Settings;
import com.tomclaw.molecus.main.views.ProjectView;
import com.tomclaw.molecus.main.views.ProjectView_;
import com.tomclaw.molecus.molecus.dto.Project;
import com.tomclaw.molecus.util.QueryBuilder;

/**
 * Created by solkin on 02.02.16.
 */
public abstract class ProjectsAdapter extends CursorRecyclerAdapter<ProjectsAdapter.ProjectViewHolder>
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private LoaderManager loaderManager;
    private int adapterId;
    private OnItemClickListener clickListener;

    public ProjectsAdapter(Context context, LoaderManager loaderManager, int adapterId) {
        super(null);
        this.context = context;
        this.loaderManager = loaderManager;
        this.adapterId = adapterId;
        loaderManager.initLoader(adapterId, null, this);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolderCursor(ProjectViewHolder holder, Cursor cursor) {
        Project project = Project.fromCursor(cursor);
        holder.bind(project);
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ProjectView projectView = ProjectView_.build(context);
        return new ProjectViewHolder(projectView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        QueryBuilder queryBuilder = configureQuery(new QueryBuilder());
        return queryBuilder.createCursorLoader(context, Settings.PROJECTS_RESOLVER_URI);
    }

    public abstract QueryBuilder configureQuery(QueryBuilder queryBuilder);

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        changeCursor(null);
    }

    public void destroy() {
        loaderManager.destroyLoader(adapterId);
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {

        private ProjectView projectView;

        public ProjectViewHolder(final ProjectView projectView) {
            super(projectView);
            this.projectView = projectView;
        }

        public void bind(Project project) {
            projectView.bind(project);
            projectView.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursor = getCursor();
                    if (cursor != null && cursor.moveToPosition(getPosition())) {
                        Project project = Project.fromCursor(cursor);
                        if (clickListener != null) {
                            clickListener.onItemClick(project);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Project project);
    }
}
