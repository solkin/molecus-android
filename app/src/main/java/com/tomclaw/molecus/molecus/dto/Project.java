package com.tomclaw.molecus.molecus.dto;

import com.tomclaw.molecus.util.Unobfuscatable;

/**
 * Created by Solkin on 04.11.2015.
 */
public class Project implements Unobfuscatable {

    private String id;
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

    public String getId() {
        return id;
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
        return cover;
    }

    public ProjectData getData() {
        return data;
    }

    public int getType() {
        return type;
    }
}
