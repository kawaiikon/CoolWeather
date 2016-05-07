package com.hongshi.pullToRefreshAndLoad.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class PullableImageView extends ImageView implements Pullable {

    private Boolean canPullDown = true, canPullUp = true;

    public PullableImageView(Context context) {
        super(context);
    }

    public PullableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        return canPullDown;
    }

    @Override
    public boolean canPullUp() {
        return canPullUp;
    }

}
