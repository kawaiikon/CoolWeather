package com.hongshi.pullToRefreshAndLoad.pullableview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created on 2016/3/17.
 */

public class PullableRecyclerView extends RecyclerView implements Pullable {

    private Boolean canPullDown = true, canPullUp = true;

    public PullableRecyclerView(Context context) {
        super(context);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
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
            LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
            if (getAdapter().getItemCount() == 0) {
                // 没有item的时候也可以下拉刷新
                return true;
            } else if (layoutManager.findFirstVisibleItemPosition() == 0
                    && getChildAt(0).getTop() >= 0) {
                // 滑到ListView的顶部了
                return true;
            } else
                return false;
        }else {
            return false;
        }
    }

    @Override
    public boolean canPullUp() {
        if (canPullUp) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
            if (getAdapter().getItemCount() == 0) {
                // 没有item的时候也可以上拉加载
                return true;
            } else if (layoutManager.findLastVisibleItemPosition() == (getAdapter().getItemCount() - 1)) {
                // 滑到底部了
                if (getChildAt(layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition()) != null
                        && getChildAt(
                        layoutManager.findLastVisibleItemPosition()
                                - layoutManager.findFirstVisibleItemPosition()).getBottom() <= getMeasuredHeight())
                    return true;
            }
            return false;
        }else {
            return false;
        }
    }
}
