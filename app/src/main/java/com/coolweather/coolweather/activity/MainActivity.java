package com.coolweather.coolweather.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.coolweather.CommonRes;
import com.coolweather.coolweather.R;
import com.coolweather.coolweather.adapter.WeatherFragmentPagerAdapter;
import com.coolweather.coolweather.fragment.WeatherFragment;
import com.coolweather.coolweather.model.AddedCity;
import com.coolweather.coolweather.util.Utility;
import com.coolweather.coolweather.view.CircleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.circle_view)
    CircleView mCircleView;
    @Bind(R.id.menu_img)
    ImageView mMenuImg;
    @Bind(R.id.city_text)
    TextView mCityText;
    @Bind(R.id.time_text)
    TextView mTimeText;

    private List<AddedCity> mList = new ArrayList<>();
    private Boolean isDelete = false;//判断是不是删除了城市

    //删除城市时刷新
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isDelete = true;
            init();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CommonRes.DELETE_CITY_BROADRECAST);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        init();
    }

    private void init() {
        ButterKnife.bind(this);
        mList = Utility.loadAddedCity(this);
        //把所有添加城市都删了的话退出后，添加一个城市
        if (mList.size() == 0){
            finish();
            return;
        }else {
            mCityText.setText(mList.get(0).getName());
        }

        List<Fragment> list = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            WeatherFragment fragment = WeatherFragment.newInstance(mList.get(i).getCountyCode(), i);
            list.add(fragment);
        }

        WeatherFragmentPagerAdapter adapter = new WeatherFragmentPagerAdapter(getSupportFragmentManager(), list);
        mViewPager.setOffscreenPageLimit(list.size());
        mViewPager.setAdapter(adapter);

        mCircleView = (CircleView) findViewById(R.id.circle_view);

        Boolean isFromChooseActivity = getIntent().getBooleanExtra("from_choose_activity", false);
        //选择了一个新的城市并且不是删除后刷新显示最后一个城市
        if (isFromChooseActivity && !isDelete) {
            mCircleView.init(mList.size(), mList.size());
            mViewPager.setCurrentItem(mList.size() - 1);
            mCityText.setText(mList.get(mList.size() - 1).getName());
        } else {
            mCircleView.init(mList.size(), 1);
            isDelete = false;
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCircleView.setCurrentPage(position + 1);
                mCityText.setText(mList.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mMenuImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_img:
                Intent intent = new Intent(MainActivity.this, AreaManagerActivity.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("position")) {
                    mViewPager.setCurrentItem(data.getIntExtra("position", 1));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
