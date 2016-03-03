package com.tomclaw.molecus.main.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tomclaw.molecus.R;
import com.tomclaw.molecus.main.ImageOptions;
import com.tomclaw.molecus.molecus.dto.UserInfo;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by solkin on 03/03/16.
 */
@EViewGroup(R.layout.subscriber_item)
public class SubscriberView extends LinearLayout {

    @ViewById
    ImageView avatar;

    @ViewById
    TextView nick;

    @ViewById
    View online;

    public SubscriberView(Context context) {
        super(context);
    }

    public SubscriberView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubscriberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindUserInfo(UserInfo userInfo) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(userInfo.getAvatar(), avatar,
                ImageOptions.getAvatarOptions());
        nick.setText(userInfo.getNick());
        online.setVisibility(userInfo.isOnline() ? VISIBLE : GONE);
    }
}
