package com.tomclaw.molecus.main.adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.tomclaw.molecus.main.views.ProgressView;
import com.tomclaw.molecus.main.views.ProgressView_;
import com.tomclaw.molecus.main.views.ProjectView;
import com.tomclaw.molecus.main.views.ProjectView_;
import com.tomclaw.molecus.molecus.dto.Project;

/**
 * Created by solkin on 03/03/16.
 */
public class EndlessProjectsAdapter extends EndlessAdapter<Project, ProjectViewHolder> {

    public EndlessProjectsAdapter(Context context, EndlessAdapterListener listener) {
        super(context, listener);
    }

    @Override
    public ProjectViewHolder createNormalHolder(ViewGroup parent) {
        ProjectView view = ProjectView_.build(getContext());
        return new ProjectViewHolder(view);
    }

    @Override
    public ProjectViewHolder createProgressHolder(ViewGroup parent) {
        ProgressView view = ProgressView_.build(getContext());
        return new ProjectViewHolder(view);
    }
}
