package com.hongshi.pullToRefreshAndLoad.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class PullableWebView extends WebView implements Pullable {

    private Boolean canPullDown = true, canPullUp = true;

    public PullableWebView(Context context) {
        super(context);
    }

    public PullableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
            if (getScrollY() >= getContentHeight() * getScale()
                    - getMeasuredHeight())
                return true;
            else
                return false;
        }else {
            return false;
        }
    }
}
