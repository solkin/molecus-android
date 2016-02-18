package com.tomclaw.molecus.core;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.tomclaw.molecus.main.Molecus;

import static com.tomclaw.molecus.main.Beans.bitmapCache;

/**
 * Created with IntelliJ IDEA.
 * User: Solkin
 * Date: 08.12.13
 * Time: 22:13
 */
public class BitmapTask extends WeakObjectTask<LazyImageView> {

    private Bitmap bitmap;
    private String hash;
    private int width, height;

    public BitmapTask(LazyImageView imageView, String hash, int width, int height) {
        super(imageView);
        this.hash = hash;
        this.width = width;
        this.height = height;
    }

    public static boolean isResetRequired(LazyImageView imageView, String hash) {
        return !TextUtils.equals(imageView.getHash(), hash);
    }

    @Override
    public void executeBackground() throws Throwable {
        LazyImageView image = getWeakObject();
        if (image != null) {
            bitmap = bitmapCache().getBitmapSync(hash, width, height, true, true);
        }
    }

    @Override
    public void onSuccessMain() {
        LazyImageView image = getWeakObject();
        // Hash may be changed in another task.
        if (image != null && bitmap != null && TextUtils.equals(image.getHash(), hash)) {
            image.setBitmap(bitmap);
        }
    }
}
