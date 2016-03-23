package com.tomclaw.molecus.main.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.tomclaw.molecus.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by solkin on 23/03/16.
 */
@EViewGroup(R.layout.progress_item)
public class ProgressView extends LinearLayout {

    @ViewById
    ProgressBar progress;

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void init() {
        // ButterKnife.inject(this);
        progress.setIndeterminateDrawable(
                new FoldingCirclesDrawable.Builder(getContext())
                        .build());
    }
}
