package com.tomclaw.molecus.molecus;

import com.tomclaw.molecus.core.Settings;

/**
 * Created by solkin on 01/03/16.
 */
public class DevHelper {

    public static String fixUrls(String url) {
        if (!url.startsWith("http")) {
            url = "http://molecus.ru/" + url;
        }
        if (Settings.DEV) {
            url = url.replace("/molecus.", "/dev.molecus.");
        }
        return url;
    }
}
