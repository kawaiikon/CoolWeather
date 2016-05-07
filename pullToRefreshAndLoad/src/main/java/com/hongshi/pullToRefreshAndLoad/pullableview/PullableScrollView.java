package com.hongshi.pullToRefreshAndLoad.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class PullableScrollView extends ScrollView implements Pullable {

    private Boolean canPullDown = true, canPullUp = true;

    public PullableScrollView(Context context) {
        super(context);
    }

    public PullableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCanPullDown(Boolean canPullDown) {
        this.canPullDown = canPullDown;
    }

    public void setCanPullUp(Boolean canPullUp) {
        this.canPullUp = canPullUp;
    }

    @Override
    public boolean canPullDown() {
        if (canPullDown) {
            if (getScrollY() == 0)
                return true;
            else
                return false;
        }else {
            return false;
        }
    }

    @Override
    public boolean canPullUp() {
        if (canPullUp) {
            if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
                return true;
            else
                return false;
        }else {
            return false;
        }
    }

}
