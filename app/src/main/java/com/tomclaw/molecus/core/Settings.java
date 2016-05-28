package com.tomclaw.molecus.core;

import android.net.Uri;

/**
 * Created by solkin on 03.11.15.
 */
public class Settings {
    public static final String LOG_TAG = "molecus-app";
    public static final boolean DEV = true;
    public static String DB_NAME = "molecus_db";
    public static int DB_VERSION = 1;
    public static String GLOBAL_AUTHORITY = "com.tomclaw.molecus.core.GlobalProvider";
    protected static String URI_PREFIX = "content://" + GLOBAL_AUTHORITY + "/";
    public static Uri REQUEST_RESOLVER_URI = Uri.parse(URI_PREFIX + GlobalProvider.REQUEST_TABLE);
    public static Uri PROJECTS_RESOLVER_URI = Uri.parse(URI_PREFIX + GlobalProvider.PROJECTS_TABLE);
}
