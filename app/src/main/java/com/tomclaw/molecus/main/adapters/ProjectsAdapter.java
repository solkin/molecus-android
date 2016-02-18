package com.tomclaw.molecus.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.tomclaw.molecus.main.views.ProjectView;
import com.tomclaw.molecus.main.views.ProjectView_;
import com.tomclaw.molecus.molecus.dto.Project;

import java.util.List;

/**
 * Created by solkin on 02.02.16.
 */
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {

    private Context context;
    private List<Project> projects;

    public ProjectsAdapter(Context context, List<Project> projects) {
        this.context = context;
        this.projects = projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProjectView view = ProjectView_.build(context);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        holder.bind(projects.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {

        private ProjectView projectView;

        public ProjectViewHolder(ProjectView itemView) {
            super(itemView);
            this.projectView = itemView;
        }

        public void bind(Project project) {
            projectView.bindProject(project);
        }
    }
}
