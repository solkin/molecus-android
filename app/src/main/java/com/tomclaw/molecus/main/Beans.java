package com.tomclaw.molecus.main;

import android.content.ContentResolver;
import android.content.Context;

import com.tomclaw.molecus.core.BitmapCache;
import com.tomclaw.molecus.core.BitmapCache_;
import com.tomclaw.molecus.core.UserHolder;
import com.tomclaw.molecus.core.UserHolder_;
import com.tomclaw.molecus.util.GsonSingleton;
import com.tomclaw.molecus.util.GsonSingleton_;

/**
 * Created by solkin on 16/02/16.
 */
public class Beans {

    private static Molecus molecus;
    private static UserHolder userHolder;
    private static BitmapCache bitmapCache;
    private static GsonSingleton gsonSingleton;

    public static void createBeans(Molecus app) {
        molecus = app;
        userHolder = UserHolder_.getInstance_(app);
        bitmapCache = BitmapCache_.getInstance_(app);
        gsonSingleton = GsonSingleton_.getInstance_(app);
    }

    public static Molecus app() {
        return molecus;
    }

    public static ContentResolver contentResolver() {
        return molecus.getContentResolver();
    }

    public static String appSession() {
        return molecus.getAppSession();
    }

    public static UserHolder userHolder() {
        return userHolder;
    }

    public static BitmapCache bitmapCache() {
        return bitmapCache;
    }

    public static GsonSingleton gsonSingleton() {
        return gsonSingleton;
    }
}
