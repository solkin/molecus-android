package com.tomclaw.molecus.main;

import android.app.Application;
import android.content.Context;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tomclaw.molecus.core.Request;
import com.tomclaw.molecus.core.RequestDispatcher;
import com.tomclaw.molecus.util.Sha256FileNameGenerator;
import com.tomclaw.molecus.util.StringUtil;

/**
 * Created by Solkin on 01.10.2015.
 */
public class Molecus extends Application {

    private String appSession;

    private RequestDispatcher shortRequestDispatcher;
    private RequestDispatcher uploadRequestDispatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        appSession = StringUtil.generateRandomString(32);
        Beans.createBeans(this);
        shortRequestDispatcher = new RequestDispatcher(Request.REQUEST_TYPE_SHORT);
        uploadRequestDispatcher = new RequestDispatcher(Request.REQUEST_TYPE_UPLOAD);
        initImageLoader(this);
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
        config.threadPriority(Thread.NORM_PRIORITY);
        // config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Sha256FileNameGenerator());
        config.diskCacheSize(75 * 1024 * 1024);
        config.memoryCacheSize(25 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        // config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public String getAppSession() {
        return appSession;
    }
}
