package com.coolweather.coolweather.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.coolweather.Prams;
import com.coolweather.coolweather.R;
import com.coolweather.coolweather.adapter.WeatherAdapter;
import com.coolweather.coolweather.model.AddedCity;
import com.coolweather.coolweather.service.AutoUpdateService;
import com.coolweather.coolweather.util.HttpCallbackListener;
import com.coolweather.coolweather.util.HttpUtil;
import com.coolweather.coolweather.util.LogUtil;
import com.coolweather.coolweather.util.Utility;
import com.hongshi.pullToRefreshAndLoad.View.PullToRefreshLayout;
import com.hongshi.pullToRefreshAndLoad.pullableview.PullableScrollView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by bian on 2016/5/5 11:32.
 */
public class WeatherFragment extends Fragment {

    private final String TAG = "WeatherFragment";
    private RecyclerView recyclerView;
    /**
     * 县级代号
     */
    private String mCountyCode;
    private PullableScrollView mPullableScrollView;
    private PullToRefreshLayout mPullLayput;

    /*
    * 用于显示发布时间
    * */
    private TextView publishText;
    /*
    * 用于显示天气描述信息
    * */
    private TextView weatherDespText;
    /**
     * 天气图标
     */
    private ImageView weatherImg;
    /*
    * 用于显示当前气温
    * */
    private TextView currentTempText;
    /*
    * 用于显示体感气温
    * */
    private TextView bodyTempText;
    /**
     * 第三天星期几
     */
    private TextView weekDayTxt;

    private Boolean isVisible = false;//判断Fragment数否显示

    private Calendar mLastRefreshTime;//上次刷新的时间戳

    private int mPage;//第几页

    public static WeatherFragment newInstance(String countyCode, int page) {
        Bundle args = new Bundle();
        args.putString("county_code", countyCode);
        args.putInt("page", page);
        WeatherFragment pageFragment = new WeatherFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCountyCode = getArguments().getString("county_code");
        mPage = getArguments().getInt("page");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mPullableScrollView = (PullableScrollView) view.findViewById(R.id.pull_scroll_view);
        mPullableScrollView.setCanPullUp(false);
        mPullLayput = (PullToRefreshLayout) view.findViewById(R.id.pull_layout);
        publishText = (TextView) getActivity().findViewById(R.id.time_text);
        weatherDespText = (TextView) view.findViewById(R.id.weather_text);
        currentTempText = (TextView) view.findViewById(R.id.wen_du_text);
        bodyTempText = (TextView) view.findViewById(R.id.ti_gan_wen_du_text);
        weatherImg = (ImageView) view.findViewById(R.id.weather_img);
        weekDayTxt = (TextView) view.findViewById(R.id.week_day_text_3);

        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        Calendar calendar = Calendar.getInstance();

        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case 1:
                weekDayTxt.setText("星期二");
                break;
            case 2:
                weekDayTxt.setText("星期三");
                break;
            case 3:
                weekDayTxt.setText("星期四");
                break;
            case 4:
                weekDayTxt.setText("星期五");
                break;
            case 5:
                weekDayTxt.setText("星期六");
                break;
            case 6:
                weekDayTxt.setText("星期日");
                break;
            case 7:
                weekDayTxt.setText("星期一");
                break;
        }

        //展示12小时每小时天气
        List<String> list = new ArrayList<>();
        for (int i = calendar.get(Calendar.HOUR_OF_DAY); i < calendar.get(Calendar.HOUR_OF_DAY) + 12; i++) {
            if (i>=24) {
                list.add("" + (i - 24));
            } else {
                list.add("" + i);
            }
        }
        recyclerView.setAdapter(new WeatherAdapter(view.getContext(), list));

        mPullLayput.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                mLastRefreshTime = Calendar.getInstance();
                queryWeatherCode(mCountyCode);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
        return view;
    }

    //最先回调setUserVisibleHint（），在页面切换时只调用setUserVisibleHint（）
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mLastRefreshTime == null || Utility.compare(mLastRefreshTime, Calendar.getInstance())) {
            if (isVisibleToUser) {
                if (mPullLayput != null) {
                    mPullLayput.autoRefresh();
                    //请求过一次后不让onResume()里再请求一次
                    isVisible = false;
                } else {
                    isVisible = true;
                }
            } else {
                isVisible = false;
            }
        }else {
            isVisible = false;
        }
    }

    //在页面切换时只调用setUserVisibleHint（），退出重进时调用onResume(）
    @Override
    public void onResume() {
        super.onResume();
        if (isVisible) {
            mPullLayput.autoRefresh();
        }
    }

    /*
     * 查询县级代号所对应的天气代号。
     * */
    private void queryWeatherCode(String countyCode) {
        String address = Prams.QUERY_AREA + countyCode + Prams.XML;
        queryFromServer(address, "countyCode");
    }

    /*
    * 查询天气代号对应的天气
    * */
    private void queryWeatherInfo(String weatherCode) {
        String address = Prams.QUERY_WEATHER + weatherCode + Prams.HTML;
//        String address = "http://api.weatherdt.com/common/?area=101010100&type=forecast[1h_2d{001,002,003,004}]&key=f2d4cfcd6e6a0267f7772524fa193f1b";
        queryFromServer(address, "weatherCode");

    }

    /*
    * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
    * */
    private void queryFromServer(final String address, final String type) {
        LogUtil.url(address);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                LogUtil.e(TAG, "response===" + response);
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    //处理服务器返回的天气信息

                    Utility.handleWeatherResponse(getActivity().getApplicationContext(), response);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPullLayput.refreshFinish(PullToRefreshLayout.SUCCEED);
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPullLayput.refreshFinish(PullToRefreshLayout.FAIL);
                    }
                });
            }
        });
    }

    /*
    * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
    * */
    private void showWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String temp1, temp2, weatherDesp;
        temp1 = preferences.getString("temp2", "").replaceAll("℃", "");
        temp2 = preferences.getString("temp1", "").replaceAll("℃", "");
        weatherDesp = preferences.getString("weather_desp", "");
        currentTempText.setText(Integer.valueOf(temp2) - 3 + "°");
        bodyTempText.setText("体感 " + (Integer.valueOf(temp2) - 3) + "℃");
        weatherDespText.setText(weatherDesp + " " + temp1 + "~" + temp2 + "℃");
        publishText.setText(preferences.getString("publish_time", "") + "发布");
        int weatherImgRes = R.drawable.sun_1;
        if (weatherDesp.contains("晴")){
            weatherImgRes = R.drawable.sun_1;
        } else if (weatherDesp.contains("阴")){
            weatherImgRes = R.drawable.yin;
        }else if (weatherDesp.contains("多云")){
            weatherImgRes = R.drawable.duo_yun;
        }else if (weatherDesp.contains("雨")){
            weatherImgRes= R.drawable.yu;
        } else if (weatherDesp.contains("雪")){
            weatherImgRes = R.drawable.xue;
        }
        weatherImg.setImageResource(weatherImgRes);
        Intent intent = new Intent(getActivity(), AutoUpdateService.class);
        getActivity().startService(intent);

        //把当前温度发布时间存到本地
        List<AddedCity> list = Utility.loadAddedCity(getActivity());
        for (AddedCity addedCity : list){
            if (addedCity.getCountyCode().equals(mCountyCode)){
                addedCity.setTime(preferences.getString("publish_time", ""));
                addedCity.setWenDu(Integer.valueOf(temp2) - 3 + "°");
            }
        }
        Utility.saveAddedCity(getActivity(), list);
        if (mPage == 0){
            Utility.sendNotification(getContext(), weatherImgRes, (Integer.valueOf(temp2) - 3) + "℃",
                    temp1 + "~" + temp2 + "℃", weatherDesp, preferences.getString("city_name", ""),
                    preferences.getString("publish_time", ""));
        }
    }
}
