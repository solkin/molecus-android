package com.tomclaw.molecus.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.tomclaw.molecus.main.views.ProjectView;
import com.tomclaw.molecus.main.views.ProjectView_;
import com.tomclaw.molecus.molecus.dto.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solkin on 02.02.16.
 */
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {

    private final List<Project> items = new ArrayList<>();
    private Context context;
    private OnItemClickListener clickListener;
    private OnItemShowListener showListener;

    public ProjectsAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setShowListener(OnItemShowListener showListener) {
        this.showListener = showListener;
    }

    public void add(List<Project> projects) {
        items.addAll(projects);
    }

    public void clear() {
        items.clear();
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProjectView projectView = ProjectView_.build(context);
        return new ProjectViewHolder(projectView);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        Project project = items.get(position);
        boolean showProgress = false;
        if (position == items.size() - 1 && showListener != null) {
            showProgress = showListener.onLastItem(position, project);
        }
        holder.bind(project, showProgress);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {

        private ProjectView projectView;

        public ProjectViewHolder(final ProjectView projectView) {
            super(projectView);
            this.projectView = projectView;
        }

        public void bind(Project project, boolean showProgress) {
            projectView.bind(project, showProgress);
            projectView.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Project project = items.get(position);
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

    public interface OnItemShowListener {
        boolean onLastItem(int position, Project project);
    }
}
