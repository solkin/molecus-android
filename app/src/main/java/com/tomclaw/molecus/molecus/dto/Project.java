package com.tomclaw.molecus.molecus.dto;

import android.content.ContentValues;
import android.database.Cursor;

import com.tomclaw.molecus.core.GlobalProvider;
import com.tomclaw.molecus.molecus.DevHelper;
import com.tomclaw.molecus.util.Unobfuscatable;

import static com.tomclaw.molecus.main.Beans.gsonSingleton;

/**
 * Created by Solkin on 04.11.2015.
 */
public class Project implements Unobfuscatable {

    private static boolean columnsInitialized = false;

    private static int COLUMN_ID;
    private static int COLUMN_TITLE;
    private static int COLUMN_USER_NICK;
    private static int COLUMN_USER_AVATAR;
    private static int COLUMN_CATEGORY;
    private static int COLUMN_TIME_ADDED;
    private static int COLUMN_LIKES;
    private static int COLUMN_VIEWS;
    private static int COLUMN_LIKED;
    private static int COLUMN_COVER;
    private static int COLUMN_DATA;
    private static int COLUMN_TYPE;

    private String id;
    private String title;
    private BaseUserInfo user;
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

    public Project(String id, String title, BaseUserInfo user, String category, long timeAdded,
                   int likes, int views, boolean liked, String cover, ProjectData data, int type) {
        this.id = id;
        this.title = title;
        this.user = user;
        this.category = category;
        this.time_added = timeAdded;
        this.likes = likes;
        this.views = views;
        this.liked = liked;
        this.cover = cover;
        this.data = data;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BaseUserInfo getUserInfo() {
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

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(GlobalProvider.PROJECT_ID, getId());
        values.put(GlobalProvider.PROJECT_TITLE, getTitle());
        values.put(GlobalProvider.PROJECT_USER_NICK, getUserInfo().getNick());
        values.put(GlobalProvider.PROJECT_USER_AVATAR, getUserInfo().getAvatar());
        values.put(GlobalProvider.PROJECT_CATEGORY, getCategory());
        values.put(GlobalProvider.PROJECT_TIME_ADDED, getTimeAdded());
        values.put(GlobalProvider.PROJECT_LIKES, getLikes());
        values.put(GlobalProvider.PROJECT_VIEWS, getViews());
        values.put(GlobalProvider.PROJECT_LIKED, isLiked() ? 1 : 0);
        values.put(GlobalProvider.PROJECT_COVER, getCover());
        values.put(GlobalProvider.PROJECT_DATA, gsonSingleton().toJson(getData()));
        values.put(GlobalProvider.PROJECT_TYPE, getType());
        return values;
    }

    public static Project fromCursor(Cursor cursor) {
        if (!columnsInitialized) {
            COLUMN_ID = cursor.getColumnIndex(GlobalProvider.PROJECT_ID);
            COLUMN_TITLE = cursor.getColumnIndex(GlobalProvider.PROJECT_TITLE);
            COLUMN_USER_NICK = cursor.getColumnIndex(GlobalProvider.PROJECT_USER_NICK);
            COLUMN_USER_AVATAR = cursor.getColumnIndex(GlobalProvider.PROJECT_USER_AVATAR);
            COLUMN_CATEGORY = cursor.getColumnIndex(GlobalProvider.PROJECT_CATEGORY);
            COLUMN_TIME_ADDED = cursor.getColumnIndex(GlobalProvider.PROJECT_TIME_ADDED);
            COLUMN_LIKES = cursor.getColumnIndex(GlobalProvider.PROJECT_LIKES);
            COLUMN_VIEWS = cursor.getColumnIndex(GlobalProvider.PROJECT_VIEWS);
            COLUMN_LIKED = cursor.getColumnIndex(GlobalProvider.PROJECT_LIKED);
            COLUMN_COVER = cursor.getColumnIndex(GlobalProvider.PROJECT_COVER);
            COLUMN_DATA = cursor.getColumnIndex(GlobalProvider.PROJECT_DATA);
            COLUMN_TYPE = cursor.getColumnIndex(GlobalProvider.PROJECT_TYPE);
            columnsInitialized = true;
        }

        return new Project(
                cursor.getString(COLUMN_ID),
                cursor.getString(COLUMN_TITLE),
                new BaseUserInfo(cursor.getString(COLUMN_USER_NICK),
                        cursor.getString(COLUMN_USER_AVATAR)),
                cursor.getString(COLUMN_CATEGORY),
                cursor.getLong(COLUMN_TIME_ADDED),
                cursor.getInt(COLUMN_LIKES),
                cursor.getInt(COLUMN_VIEWS),
                cursor.getInt(COLUMN_LIKED) == 1,
                cursor.getString(COLUMN_COVER),
                gsonSingleton().fromJson(cursor.getString(COLUMN_DATA), ProjectData.class),
                cursor.getInt(COLUMN_TYPE)
        );
    }
}
