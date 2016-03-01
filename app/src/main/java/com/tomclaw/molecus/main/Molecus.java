package com.tomclaw.molecus.main;

import android.content.Context;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orm.SugarApp;
import com.tomclaw.molecus.core.Request;
import com.tomclaw.molecus.core.RequestDispatcher;
import com.tomclaw.molecus.util.Sha256FileNameGenerator;

/**
 * Created by Solkin on 01.10.2015.
 */
public class Molecus extends SugarApp {

    static Molecus instance;

    private RequestDispatcher shortRequestDispatcher;
    private RequestDispatcher uploadRequestDispatcher;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        Beans.createBeans(this);
        shortRequestDispatcher = new RequestDispatcher(Request.REQUEST_TYPE_SHORT);
        uploadRequestDispatcher = new RequestDispatcher(Request.REQUEST_TYPE_UPLOAD);
        initImageLoader(this);
    }

    public static Molecus getInstance() {
        return instance;
    }

    public RequestDispatcher getShortRequestDispatcher() {
        return shortRequestDispatcher;
    }

    public RequestDispatcher getUploadRequestDispatcher() {
        return uploadRequestDispatcher;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        // config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Sha256FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.memoryCacheSize(10 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
