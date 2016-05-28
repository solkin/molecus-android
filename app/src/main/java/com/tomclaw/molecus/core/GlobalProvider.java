package com.tomclaw.molecus.core;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.tomclaw.molecus.molecus.dto.Project;
import com.tomclaw.molecus.util.Logger;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 4/23/13
 * Time: 12:53 AM
 */
public class GlobalProvider extends ContentProvider {

    // Methods
    public static final String METHOD_PROJECTS_MERGE = "projects_merge";

    // Table
    public static final String REQUEST_TABLE = "requests";
    public static final String PROJECTS_TABLE = "projects";

    // Fields
    public static final String ROW_AUTO_ID = "_id";

    public static final String REQUEST_TYPE = "request_type";
    public static final String REQUEST_CLASS = "request_class";
    public static final String REQUEST_SESSION = "request_session";
    public static final String REQUEST_PERSISTENT = "request_persistent";
    public static final String REQUEST_STATE = "request_state";
    public static final String REQUEST_BUNDLE = "request_bundle";
    public static final String REQUEST_TAG = "request_tag";

    public static final String PROJECT_ID = "project_id";
    public static final String PROJECT_TITLE = "project_title";
    public static final String PROJECT_USER_NICK = "user_nick";
    public static final String PROJECT_USER_AVATAR = "user_avatar";
    public static final String PROJECT_CATEGORY = "project_category";
    public static final String PROJECT_TIME_ADDED = "project_time_added";
    public static final String PROJECT_LIKES = "project_likes";
    public static final String PROJECT_VIEWS = "project_views";
    public static final String PROJECT_LIKED = "project_liked";
    public static final String PROJECT_COVER = "project_cover";
    public static final String PROJECT_DATA = "project_data";
    public static final String PROJECT_TYPE = "project_type";

    // Database create scripts.
    protected static final String DB_CREATE_REQUEST_TABLE_SCRIPT = "create table " + REQUEST_TABLE + "("
            + ROW_AUTO_ID + " integer primary key autoincrement, " + REQUEST_TYPE + " int, "
            + REQUEST_CLASS + " text, " + REQUEST_SESSION + " text, "
            + REQUEST_PERSISTENT + " int, " + REQUEST_STATE + " int, "
            + REQUEST_BUNDLE + " text, " + REQUEST_TAG + " text" + ");";

    protected static final String DB_CREATE_PROJECTS_TABLE_SCRIPT = "create table " + PROJECTS_TABLE + "("
            + ROW_AUTO_ID + " integer primary key autoincrement, "
            + PROJECT_ID + " text unique, "
            + PROJECT_TITLE + " text, "
            + PROJECT_USER_NICK + " text, "
            + PROJECT_USER_AVATAR + " text, "
            + PROJECT_CATEGORY + " text, "
            + PROJECT_TIME_ADDED + " int, "
            + PROJECT_LIKES + " int, "
            + PROJECT_VIEWS + " int, "
            + PROJECT_LIKED + " int, "
            + PROJECT_COVER + " text, "
            + PROJECT_DATA + " text, "
            + PROJECT_TYPE + " int" + ");";

    protected static final String DB_CREATE_PROJECTS_INDEX_SCRIPT = "CREATE INDEX Idx0 ON " +
            GlobalProvider.PROJECTS_TABLE + "(" +
            GlobalProvider.PROJECT_ID +
            ");";

    public static final int ROW_INVALID = -1;

    public static final String KEY_PROJECTS = "projects";

    // Database helper object.
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    // URI id.
    private static final int URI_REQUEST = 1;
    private static final int URI_PROJECTS = 2;

    // URI tool instance.
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Settings.GLOBAL_AUTHORITY, REQUEST_TABLE, URI_REQUEST);
        uriMatcher.addURI(Settings.GLOBAL_AUTHORITY, PROJECTS_TABLE, URI_PROJECTS);
    }

    @Override
    public boolean onCreate() {
        Logger.log("GlobalProvider onCreate");
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table;
        String groupBy = null;
        String rawQuery = null;
        switch (uriMatcher.match(uri)) {
            case URI_REQUEST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ROW_AUTO_ID + " ASC";
                }
                table = REQUEST_TABLE;
                break;
            case URI_PROJECTS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ROW_AUTO_ID + " ASC";
                }
                table = PROJECTS_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor;
        if (TextUtils.isEmpty(rawQuery)) {
            cursor = sqLiteDatabase.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder);
        } else {
            cursor = sqLiteDatabase.rawQuery(rawQuery, null);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        Logger.log("getType, " + uri.toString());
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        long rowId = sqLiteDatabase.insert(getTableName(uri), null, values);
        Uri resultUri = ContentUris.withAppendedId(uri, rowId);
        // Notify ContentResolver about data changes.
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        int rows = sqLiteDatabase.delete(getTableName(uri), selection, selectionArgs);
        // Notify ContentResolver about data changes.
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        int rows = sqLiteDatabase.update(getTableName(uri), values, selection, selectionArgs);
        // Notify ContentResolver about data changes.
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Bundle call(String method, String arg, Bundle extras) {
        if (TextUtils.equals(method, METHOD_PROJECTS_MERGE)) {
            ArrayList<Project> projects = (ArrayList<Project>) extras.getSerializable(KEY_PROJECTS);
            if (projects != null && !projects.isEmpty()) {
                try {
                    sqLiteDatabase.beginTransaction();
                    for (Project project : projects) {
                        ContentValues values = project.getContentValues();
                        sqLiteDatabase.insertWithOnConflict(PROJECTS_TABLE, null, values,
                                SQLiteDatabase.CONFLICT_IGNORE);
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                    getContext().getContentResolver().notifyChange(Settings.PROJECTS_RESOLVER_URI, null);
                } finally {
                    sqLiteDatabase.endTransaction();
                }
            }
        }
        return null;
    }

    private static String getTableName(Uri uri) {
        String table;
        switch (uriMatcher.match(uri)) {
            case URI_REQUEST:
                table = REQUEST_TABLE;
                break;
            case URI_PROJECTS:
                table = PROJECTS_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        return table;
    }
}
