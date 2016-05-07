package com.hongshi.pullToRefreshAndLoad.pullableview;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class PullableTextView extends TextView implements Pullable {

    private Boolean canPullDown = true, canPullUp = true;

    public PullableTextView(Context context) {
        super(context);
    }

    public PullableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableTextView(Context context, AttributeSet attrs, int defStyle) {
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
