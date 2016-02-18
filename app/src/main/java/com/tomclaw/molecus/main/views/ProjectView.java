package com.tomclaw.molecus.main.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tomclaw.molecus.R;
import com.tomclaw.molecus.molecus.dto.Project;
import com.tomclaw.molecus.util.TimeHelper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by solkin on 17/02/16.
 */
@EViewGroup(R.layout.project_item)
public class ProjectView extends LinearLayout {

    @ViewById
    ImageView cover;

    @ViewById
    TextView title;

    @ViewById
    TextView userNick;

    @ViewById
    ImageView userAvatar;

    @ViewById
    TextView viewsCount;

    @ViewById
    TextView likesCount;

    @ViewById
    TextView publishDate;

    @Bean
    TimeHelper timeHelper;

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

    public ProjectView(Context context) {
        super(context);
    }

    public ProjectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProjectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindProject(Project project) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(project.getCover(), cover, coverOptions);
        title.setText(project.getTitle());
        imageLoader.displayImage(project.getUserInfo().getAvatar(), userAvatar, avatarOptions);
        userNick.setText(project.getUserInfo().getNick());
        viewsCount.setText(String.valueOf(project.getViews()));
        likesCount.setText(String.valueOf(project.getLikes()));
        publishDate.setText(timeHelper.getFormattedDate(project.getTimeAdded() * 1000));
    }
}
