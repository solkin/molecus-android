package com.tomclaw.molecus.main.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tomclaw.molecus.R;
import com.tomclaw.molecus.main.ImageOptions;
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
        imageLoader.displayImage(project.getCover(), cover, ImageOptions.getCoverOptions());
        title.setText(project.getTitle());
        imageLoader.displayImage(project.getUserInfo().getAvatar(), userAvatar,
                ImageOptions.getAvatarOptions());
        userNick.setText(project.getUserInfo().getNick());
        viewsCount.setText(String.valueOf(project.getViews()));
        likesCount.setText(String.valueOf(project.getLikes()));
        publishDate.setText(timeHelper.getFormattedDate(project.getTimeAdded() * 1000));
    }
}
