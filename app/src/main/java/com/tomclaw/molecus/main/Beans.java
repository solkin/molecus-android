package com.tomclaw.molecus.main;

import android.content.Context;

import com.tomclaw.molecus.core.BitmapCache;
import com.tomclaw.molecus.core.BitmapCache_;
import com.tomclaw.molecus.core.UserHolder;
import com.tomclaw.molecus.core.UserHolder_;

/**
 * Created by solkin on 16/02/16.
 */
public class Beans {

    private static UserHolder userHolder;
    private static BitmapCache bitmapCache;

    public static void createBeans(Context context) {
        context = context.getApplicationContext();
        userHolder = UserHolder_.getInstance_(context);
        bitmapCache = BitmapCache_.getInstance_(context);
    }

    public static UserHolder userHolder() {
        return userHolder;
    }

    public static BitmapCache bitmapCache() {
        return bitmapCache;
    }
}
