package com.coolweather.coolweather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.coolweather.coolweather.R;

/**
 * Created by bian on 2016/5/5 15:02.
 */
public class CircleView extends LinearLayout {

    private Context mContext;
    private int mPages, mCurrentPage;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPages = 5;
        init(mPages);
    }

    private void init(final int pages) {
        this.setOrientation(HORIZONTAL);
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = getHeight();
                ImageView imageView;
                LayoutParams image_params = new LayoutParams(
                        height, ViewGroup.LayoutParams.MATCH_PARENT);
                if (pages > 0) {
                    //第一个点
                    mCurrentPage = 1;
                    imageView = new ImageView(mContext);
                    imageView.setImageResource(R.drawable.white_circle);
                    addView(imageView, image_params);
                    //后面的点
                    for (int i = 0; i < pages - 1; i++) {
                        imageView = new ImageView(mContext);
                        imageView.setImageResource(R.drawable.gray_circle);
                        image_params.setMarginStart(height / 2);
                        addView(imageView, image_params);
                    }

                }
            }
        });
    }

    public void setCurrentPage(int currentPage) {
        if (mPages < currentPage) {
            Toast.makeText(mContext, "当前页不能大于总页数", Toast.LENGTH_SHORT).show();
        } else {
            ImageView imageView;
            imageView = (ImageView) this.getChildAt(currentPage - 1);
            imageView.setImageResource(R.drawable.white_circle);
            imageView = (ImageView) this.getChildAt(mCurrentPage - 1);
            imageView.setImageResource(R.drawable.gray_circle);
            mCurrentPage = currentPage;
        }
    }

}
