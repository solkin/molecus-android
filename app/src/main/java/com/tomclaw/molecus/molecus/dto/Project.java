package com.tomclaw.molecus.molecus.dto;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Table;
import com.orm.dsl.Unique;
import com.tomclaw.molecus.molecus.DevHelper;
import com.tomclaw.molecus.util.ExcludeField;
import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created by Solkin on 04.11.2015.
 */
@Table
public class Project implements Unobfuscatable {

    @ExcludeField
    private Long id;

    @SerializedName("id")
    @Unique
    private String projectId;
    private String title;
    private UserInfo user;
    private String category;
    private long time_added;
    private int likes;
    private int views;
    private boolean liked;
    private String cover;
    private ProjectData data;
    private int type;

    public Project() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public UserInfo getUserInfo() {
        return user;
    }

    public String getCategory() {
        return category;
    }

    public long getTimeAdded() {
        return time_added;
    }

    public int getLikes() {
        return likes;
    }

    public int getViews() {
        return views;
    }

    public boolean isLiked() {
        return liked;
    }

    public String getCover() {
        return DevHelper.fixUrls(cover);
    }

    public ProjectData getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    public void setUserInfo(UserInfo user) {
        this.user = user;
    }
}
