package com.tomclaw.molecus.main.adapters;

import android.view.View;

import com.tomclaw.molecus.main.views.ProjectView;
import com.tomclaw.molecus.molecus.dto.Project;

/**
 * Created by solkin on 03/03/16.
 */
public class ProjectViewHolder extends EndlessAdapter.ItemViewHolder<Project> {

    public ProjectViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindNormal(Project item) {
        ((ProjectView) itemView).bind(item);
    }

    @Override
    public void bindProgress() {
    }
}
