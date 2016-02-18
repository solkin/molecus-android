package com.tomclaw.molecus.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.tomclaw.molecus.main.Molecus;
import com.tomclaw.molecus.util.BitmapHelper;
import com.tomclaw.molecus.util.Logger;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: solkin
 * Date: 12/5/13
 * Time: 1:32 AM
 */
@EBean(scope = EBean.Scope.Singleton)
public class BitmapCache {

    @Bean
    TaskExecutor taskExecutor;

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters
    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final int BITMAP_SIZE_ORIGINAL = 0;
    private static final String BITMAP_CACHE_FOLDER = "bitmaps";
    private File path;
    private LruCache<String, Bitmap> bitmapLruCache;
    private int densityDpi;

    public BitmapCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        // Use 1/12th of the available memory for this memory cache.
        int cacheSize = maxMemory / 12;
        bitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void init(Context context) {
        path = context.getDir(BITMAP_CACHE_FOLDER, Context.MODE_PRIVATE);
        densityDpi = context.getResources().getDisplayMetrics().densityDpi;
    }

    private static String getCacheKey(String hash, int width, int height) {
        return hash + "_" + width + "_" + height;
    }

    private static boolean isCacheKeyFromHash(String cacheKey, String hash) {
        return !TextUtils.isEmpty(cacheKey) && !TextUtils.isEmpty(hash) && cacheKey.startsWith(hash + "_");
    }

    /**
     * Setup required image by hash on specified image view in background thread.
     * While image loading from disk cache and scaling, on image view will be placed default resource.
     *
     * @param imageView       - image view to show image
     * @param hash            - required image hash
     * @param defaultResource - default resource to show while original image being loaded and scaled
     * @param original        - if false, image will be cached with specified imageView size,
     *                        if true original size image will be used
     */
    public void getBitmapAsync(LazyImageView imageView, final String hash, int defaultResource, boolean original) {
        int width, height;
        if (original) {
            width = height = BITMAP_SIZE_ORIGINAL;
        } else {
            width = imageView.getWidth();
            height = imageView.getHeight();
        }
        getBitmapAsync(imageView, hash, defaultResource, width, height);
    }

    /**
     * Setup required image by hash on specified image view in background thread.
     * While image loading from disk cache and scaling, on image view will be placed default resource.
     *
     * @param imageView       - image view to show image
     * @param hash            - required image hash
     * @param defaultResource - default resource to show while original image being loaded and scaled
     * @param width           - interested bitmap width
     * @param height          - interested bitmap height
     */
    public void getBitmapAsync(LazyImageView imageView, final String hash, int defaultResource, int width, int height) {
        Bitmap bitmap = getBitmapSyncFromCache(hash, width, height);
        // Checking for there is no cached bitmap and reset is really required.
        if (TextUtils.isEmpty(hash) || (bitmap == null && BitmapTask.isResetRequired(imageView, hash))) {
            imageView.setPlaceholder(defaultResource);
        }
        imageView.setHash(hash);
        if (!TextUtils.isEmpty(hash)) {
            // Checking for bitmap cached or not.
            if (bitmap == null) {
                taskExecutor.execute(new BitmapTask(imageView, hash, width, height));
            } else {
                imageView.setBitmap(bitmap);
            }
        }
    }

    public void getThumbnailAsync(LazyImageView imageView, String hash, long imageId, int placeholder) {
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        Bitmap bitmap = getBitmapSyncFromCache(hash, width, height);
        // Checking for there is no cached bitmap and reset is really required.
        if (bitmap == null && ThumbnailTask.isResetRequired(imageView, hash)) {
            imageView.setPlaceholder(placeholder);
        }
        imageView.setHash(hash);
        if (!TextUtils.isEmpty(hash)) {
            // Checking for bitmap cached or not.
            if (bitmap == null) {
                taskExecutor.execute(new ThumbnailTask(imageView, hash, imageId, width, height));
            } else {
                imageView.setBitmap(bitmap);
            }
        }
    }

    public Bitmap getBitmapSyncFromCache(String hash, int width, int height) {
        String cacheKey = getCacheKey(hash, width, height);
        return bitmapLruCache.get(cacheKey);
    }

    /**
     * Returns bitmap from memory LRU cache or load image
     * from disk first if there is no image in memory cache.
     * Image of every size will be loaded from disk, scaled and cached!
     *
     * @param hash           - required image hash
     * @param width          - required width
     * @param height         - required height
     * @param isProportional - proportional scale flag
     * @param isAccurate     - bitmap may be sampled size or exact width and height
     * @return Bitmap or null if such image not found.
     */
    public Bitmap getBitmapSync(String hash, int width, int height, boolean isProportional, boolean isAccurate) {
        String cacheKey = getCacheKey(hash, width, height);
        Bitmap bitmap = bitmapLruCache.get(cacheKey);
        if (bitmap == null) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(getBitmapFilePath(hash));
                if (width != BITMAP_SIZE_ORIGINAL && height != BITMAP_SIZE_ORIGINAL) {
                    bitmap = BitmapHelper.decodeSampledBitmapFromStream(inputStream, width, height);
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    // Check and set original size.
                    if (width == BITMAP_SIZE_ORIGINAL) {
                        width = bitmap.getWidth();
                    }
                    if (height == BITMAP_SIZE_ORIGINAL) {
                        height = bitmap.getHeight();
                    }
                }
                // Checking for exact size is needed.
                if (isAccurate) {
                    // Resize bitmap for the largest size.
                    if (isProportional) {
                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            height = width * bitmap.getHeight() / bitmap.getWidth();
                        } else if (bitmap.getHeight() > bitmap.getWidth()) {
                            width = height * bitmap.getWidth() / bitmap.getHeight();
                        }
                    }
                    // Check for bitmap needs to be resized.
                    if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        bitmap.recycle();
                        bitmap = scaledBitmap;
                    }
                }
                cacheBitmap(cacheKey, bitmap);
            } catch (FileNotFoundException ignored) {
                Logger.log("Bitmap '" + hash + "' not found!");
            } catch (Throwable ex) {
                Logger.log("Couldn't cache '" + hash + "' bitmap!", ex);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return bitmap;
    }

    public boolean saveBitmapSync(String hash, Bitmap bitmap) {
        return saveBitmapSync(hash, bitmap, COMPRESS_FORMAT);
    }

    public boolean saveBitmapSync(String hash, Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        try {
            OutputStream os = new FileOutputStream(getBitmapFilePath(hash));
            bitmap.compress(compressFormat, 65, os);
            os.flush();
            os.close();
            return true;
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Logger.log("Error writing bitmap: " + hash, e);
        }
        return false;
    }

    public void saveBitmapAsync(String hash, Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        taskExecutor.execute(new SaveBitmapTask(hash, bitmap, compressFormat));
    }

    public void cacheBitmapOriginal(String hash, Bitmap bitmap) {
        String cacheKey = getCacheKey(hash, BITMAP_SIZE_ORIGINAL, BITMAP_SIZE_ORIGINAL);
        cacheBitmap(cacheKey, bitmap);
    }

    public void cacheBitmap(String cacheKey, Bitmap bitmap) {
        bitmapLruCache.put(cacheKey, bitmap);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void invalidateHash(String hash) {
        // Remove file first.
        new File(getBitmapFilePath(hash)).delete();
        // Create Lru cache snapshot.
        Set<String> cacheKeySet = bitmapLruCache.snapshot().keySet();
        // Find and remove cache keys, assigned with specified hash.
        for (String cacheKey : cacheKeySet) {
            if (isCacheKeyFromHash(cacheKey, hash)) {
                bitmapLruCache.remove(cacheKey);
            }
        }

    }

    public String getBitmapFilePath(String hash) {
        return path.getPath().concat("/").concat(hash).concat(".").concat(COMPRESS_FORMAT.name());
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

    public boolean isLowDensity() {
        return densityDpi == DisplayMetrics.DENSITY_LOW ||
                densityDpi == DisplayMetrics.DENSITY_MEDIUM;
    }

    private static byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hash;
    }

    public static String getHash(String path) {
        byte[] md5 = getMD5(path.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX);
    }

    public class SaveBitmapTask extends Task {

        private String hash;
        private Bitmap bitmap;
        private Bitmap.CompressFormat compressFormat;

        public SaveBitmapTask(String hash, Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
            this.hash = hash;
            this.bitmap = bitmap;
            this.compressFormat = compressFormat;
        }

        @Override
        public void executeBackground() throws Throwable {
            saveBitmapSync(hash, bitmap, compressFormat);
        }
    }
}