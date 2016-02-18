package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.core.Response;
import com.tomclaw.molecus.molecus.dto.Project;

import java.util.List;

/**
 * Created by Solkin on 18.11.2015.
 */
public class ProjectsResponse extends Response {

    private int total;
    private List<Project> projects;

    public ProjectsResponse(int status) {
        super(status);
    }

    public ProjectsResponse(int status, int total, List<Project> projects) {
        super(status);
        this.total = total;
        this.projects = projects;
    }

    public int getTotal() {
        return total;
    }

    public List<Project> getProjects() {
        return projects;
    }
}
