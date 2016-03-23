package com.tomclaw.molecus.molecus;

import com.google.gson.reflect.TypeToken;
import com.tomclaw.molecus.molecus.dto.Project;
import com.tomclaw.molecus.util.HttpParamsBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.tomclaw.molecus.main.Beans.gsonSingleton;

/**
 * Created by Solkin on 03.11.2015.
 */
public abstract class ProjectsRequest extends MolecusRequest<ProjectsResponse> {

    private ProjectsType type;
    private int offset, count;

    public ProjectsRequest(ProjectsType type, int offset, int count) {
        this.type = type;
        this.offset = offset;
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }

    @Override
    protected String getApiName() {
        return "projects";
    }

    @Override
    public boolean isUserBased() {
        return true;
    }

    @Override
    protected final void appendParams(HttpParamsBuilder builder) {
        builder.appendParam("type", type.toString())
                .appendParam("offset", offset)
                .appendParam("count", count);
        appendProjectsParams(builder);
    }

    protected abstract void appendProjectsParams(HttpParamsBuilder builder);

    @Override
    protected ProjectsResponse parseJson(JSONObject response) throws JSONException, RequestPendingException {
        int status = response.getInt("status");
        if (status == 200) {
            int total = response.getInt("total");
            String projectsJson = response.getJSONArray("projects").toString();
            Type listType = new TypeToken<ArrayList<Project>>() {}.getType();
            List<Project> projects = gsonSingleton().fromJson(projectsJson, listType);
            return new ProjectsResponse(status, total, projects);
        }
        return new ProjectsResponse(status);
    }

    protected enum ProjectsType {
        scene,
        voting,
        list
    }
}
