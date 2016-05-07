package com.coolweather.coolweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.coolweather.R;
import com.coolweather.coolweather.view.CircleView;
import com.coolweather.coolweather.adapter.WeatherFragmentPagerAdapter;
import com.coolweather.coolweather.fragment.WeatherFragment;
import com.coolweather.coolweather.model.AddedCity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AddedCity addedCity = new AddedCity();
        addedCity.setName("杭州");
        addedCity.setTime("15:35");
        mList.add(addedCity);
        addedCity = new AddedCity();
        addedCity.setName("武汉");
        addedCity.setTime("15:35");
        mList.add(addedCity);
        addedCity = new AddedCity();
        addedCity.setName("上海");
        addedCity.setTime("15:35");
        mList.add(addedCity);
        addedCity = new AddedCity();
        addedCity.setName("江苏");
        addedCity.setTime("15:35");
        mList.add(addedCity);
        addedCity = new AddedCity();
        addedCity.setName("广州");
        addedCity.setTime("15:35");
        mList.add(addedCity);

        WeatherFragment fragment1 = WeatherFragment.newInstance("杭州");
        WeatherFragment fragment2 = WeatherFragment.newInstance("武汉");
        WeatherFragment fragment3 = WeatherFragment.newInstance("上海");
        WeatherFragment fragment4 = WeatherFragment.newInstance("江苏");
        WeatherFragment fragment5 = WeatherFragment.newInstance("广州");

        List<Fragment> list = new ArrayList<>();
        list.add(fragment1);
        list.add(fragment2);
        list.add(fragment3);
        list.add(fragment4);
        list.add(fragment5);
        WeatherFragmentPagerAdapter adapter = new WeatherFragmentPagerAdapter(getSupportFragmentManager(), list);
        mViewPager.setOffscreenPageLimit(list.size());
        mViewPager.setAdapter(adapter);

        mCircleView = (CircleView) findViewById(R.id.circle_view);

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
        switch (v.getId()){
            case R.id.menu_img:
                Intent intent = new Intent(MainActivity.this, AreaManagerActivity.class);
                intent.putParcelableArrayListExtra("added_city_list", (ArrayList<? extends Parcelable>) mList);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }
    }
}
