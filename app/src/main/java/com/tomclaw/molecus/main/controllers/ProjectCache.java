package com.tomclaw.molecus.main.controllers;

import android.support.annotation.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orm.SugarRecord;
import com.tomclaw.molecus.molecus.dto.Project;
import com.tomclaw.molecus.molecus.dto.ProjectData;
import com.tomclaw.molecus.molecus.dto.UserInfo;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SupposeBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.tomclaw.molecus.core.Serials.SUGAR;

/**
 * Created by solkin on 02/03/16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ProjectCache {

    @Bean
    UserInfoCache userInfoCache;

    private final LoadingCache<String, Project> projectsCache = CacheBuilder.newBuilder()
            .initialCapacity(128)
            .build(new CacheLoader<String, Project>() {
                @Override
                public Project load(@NonNull String key) throws Exception {
                    List<Project> projects = SugarRecord.find(Project.class, "id = ?", key);
                    if (projects.isEmpty()) {
                        throw new NoProjectsException();
                    }
                    return projects.get(0);
                }
            });

    @Background(serial = SUGAR)
    public void getProjectsRandom(int count, ProjectsCallback callback) {
        List<Project> projects = getProjectsRandomSync(count);
        callback.onProjects(projects);
    }

    @Background(serial = SUGAR)
    public void getProjectsByLimit(int limit, ProjectsCallback callback) {
        List<Project> projects = getProjectsByLimitSync(limit);
        callback.onProjects(projects);
    }

    @Background(serial = SUGAR)
    public void getProjectsByUserInfo(long userInfoId, ProjectsCallback callback) {
        List<Project> projects = getProjectsByUserInfoSync(userInfoId);
        callback.onProjects(projects);
    }

    @SupposeBackground(serial = SUGAR)
    List<Project> getProjectsRandomSync(int count) {
        List<Project> projects = SugarRecord.find(Project.class, null, null, null, "RANDOM()", String.valueOf(count));
        mergeCached(projects);
        return projects;
    }

    @SupposeBackground(serial = SUGAR)
    List<Project> getProjectsByLimitSync(int limit) {
        List<Project> projects = SugarRecord.find(Project.class, null, null, null, null, String.valueOf(limit));
        mergeCached(projects);
        return projects;
    }

    @SupposeBackground(serial = SUGAR)
    List<Project> getProjectsByUserInfoSync(long userInfoId) {
        List<Project> projects = SugarRecord.find(Project.class, "user = ?", Long.toString(userInfoId));
        mergeCached(projects);
        return projects;
    }

    @Background(serial = SUGAR)
    public void saveProjects(List<Project> projects, ProjectsCallback callback) {
        mergeCached(projects);

        List<ProjectData> projectDataList = new ArrayList<>();
        Map<String, UserInfo> userInfoMap = new HashMap<>();
        for (Project project : projects) {
            UserInfo userInfo = project.getUserInfo();
            String nick = userInfo.getNick();
            try {
                userInfo = userInfoCache.getUserInfoSync(nick);
            } catch (UserInfoCache.UserInfoNotFoundException ex) {
                UserInfo storedInfo = userInfoMap.get(nick);
                if (storedInfo != null) {
                    userInfo = storedInfo;
                }
            }
            userInfoMap.put(nick, userInfo);
            project.setUserInfo(userInfo);
            projectDataList.add(project.getData());
        }

        userInfoCache.save(userInfoMap);

        SugarRecord.saveInTx(projectDataList);
        SugarRecord.saveInTx(projects);

        callback.onProjects(projects);
    }

    @SupposeBackground(serial = SUGAR)
    void mergeCached(List<Project> projects) {
        for (int c = 0; c < projects.size(); c++) {
            Project project = projects.get(c);
            String projectId = project.getProjectId();
            try {
                Project storedProject = projectsCache.get(projectId);
                storedProject.update(project);
                projects.set(c, storedProject);
            } catch (ExecutionException e) {
                projectsCache.put(projectId, project);
            }
        }
    }

    public interface ProjectsCallback {
        void onProjects(List<Project> projects);
    }

    private class NoProjectsException extends Exception {
    }
}
