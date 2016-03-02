package com.tomclaw.molecus.main;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tomclaw.molecus.R;

/**
 * Created by solkin on 02/03/16.
 */
public class ImageOptions {

    private static DisplayImageOptions coverOptions;
    private static DisplayImageOptions avatarOptions;

    static {
        coverOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.def_cover)
                .showImageForEmptyUri(R.mipmap.def_cover)
                .showImageOnFail(R.mipmap.def_cover)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        avatarOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.def_avatar)
                .showImageForEmptyUri(R.mipmap.def_avatar)
                .showImageOnFail(R.mipmap.def_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(999))
                .build();
    }

    public static DisplayImageOptions getCoverOptions() {
        return coverOptions;
    }

    public static DisplayImageOptions getAvatarOptions() {
        return avatarOptions;
    }
}
