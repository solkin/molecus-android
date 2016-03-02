package com.tomclaw.molecus.main.controllers;

import android.support.annotation.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orm.SugarRecord;
import com.tomclaw.molecus.molecus.dto.Project;
import com.tomclaw.molecus.molecus.dto.UserInfo;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SupposeBackground;

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
    public void getProjects(long userInfoId, ProjectsCallback callback) {
        List<Project> projects = getProjectsSync(userInfoId);
        callback.onSaved(projects);
    }

    @SupposeBackground(serial = SUGAR)
    public List<Project> getProjectsSync(long userInfoId) {
        List<Project> projects = SugarRecord.find(Project.class, "user = ?", Long.toString(userInfoId));
        mergeCached(projects);
        return projects;
    }

    @Background(serial = SUGAR)
    public void saveProjects(List<Project> projects, ProjectsCallback callback) {
        mergeCached(projects);

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
        }

        userInfoCache.save(userInfoMap);

        SugarRecord.saveInTx(projects);

        callback.onSaved(projects);
    }

    @SupposeBackground(serial = SUGAR)
    void mergeCached(List<Project> projects) {
        for (int c = 0; c < projects.size(); c++) {
            Project project = projects.get(c);
            String projectId = project.getProjectId();
            try {
                Project storedProject = projectsCache.get(projectId);
                projects.set(c, storedProject);
            } catch (ExecutionException e) {
                projectsCache.put(projectId, project);
            }
        }
    }

    public interface ProjectsCallback {
        void onSaved(List<Project> projects);
    }

    private class NoProjectsException extends Exception {
    }
}
