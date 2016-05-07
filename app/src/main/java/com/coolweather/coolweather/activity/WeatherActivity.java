package com.coolweather.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.coolweather.Prams;
import com.coolweather.coolweather.R;
import com.coolweather.coolweather.service.AutoUpdateService;
import com.coolweather.coolweather.util.HttpCallbackListener;
import com.coolweather.coolweather.util.HttpUtil;
import com.coolweather.coolweather.util.LogUtil;
import com.coolweather.coolweather.util.Utility;

/**
 * Created on 2016/4/4.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{

    private LinearLayout weatherInfoLayout;
    /*
    * 用于显示城市名
    * */
    private TextView cityNameText;
    /*
    * 用于显示发布时间
    * */
    private TextView publishText;
    /*
    * 用于显示天气描述信息
    * */
    private TextView weatherDespText;
    /*
    * 用于显示气温1
    * */
    private TextView temp1Text;
    /*
    * 用于显示气温2
    * */
    private TextView temp2Text;
    /*
    * 用于显示当前日期
    * */
    private TextView currentDataText;
    /*
    * 切换城市按钮
    * */
    private Button swtichCity;
    /*
    * 更新天气按钮
    * */
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

//        Log.e("onCreate", "onCreate................");
        //初始化各控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDataText = (TextView) findViewById(R.id.current_data);
        swtichCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        swtichCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            //有县级代号时就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else {
            //没有县级代号时就直接显示本地天气
            showWeather();
        }
    }

    /*
    * 查询县级代号所对应的天气代号。
    * */
    private void queryWeatherCode(String countyCode){
        String address = Prams.QUERY_AREA + countyCode + Prams.XML;
        queryFromServer(address, "countyCode");
    }

    /*
    * 查询天气代号对应的天气
    * */
    private void queryWeatherInfo(String weatherCode){
        String address = Prams.QUERY_WEATHER + weatherCode + Prams.HTML;
//        String address = "http://api.weatherdt.com/common/?area=101010100&type=forecast[1h_2d{001,002,003,004}]&key=f2d4cfcd6e6a0267f7772524fa193f1b";
        queryFromServer(address, "weatherCode");

    }

    /*
    * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
    * */
    private void queryFromServer(final String address, final String type){
        LogUtil.url(address);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                LogUtil.response(response);
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
                    Utility.handleWeatherResponse(getApplicationContext(), response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /*
    * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
    * */
    private void showWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(preferences.getString("city_name", ""));
        temp1Text.setText(preferences.getString("temp1", ""));
        temp2Text.setText(preferences.getString("temp2", ""));
        weatherDespText.setText(preferences.getString("weather_desp", ""));
        publishText.setText("今天" + preferences.getString("publish_time", "") + "发布");
        currentDataText.setText(preferences.getString("current_data", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = preferences.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
        }
    }
}
