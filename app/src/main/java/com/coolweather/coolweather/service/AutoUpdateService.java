package com.coolweather.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.coolweather.coolweather.Prams;
import com.coolweather.coolweather.R;
import com.coolweather.coolweather.receiver.AutoUpdateReceiver;
import com.coolweather.coolweather.util.HttpCallbackListener;
import com.coolweather.coolweather.util.HttpUtil;
import com.coolweather.coolweather.util.LogUtil;
import com.coolweather.coolweather.util.Utility;

/**
 * Created on 2016/4/4.
 */
public class AutoUpdateService extends Service {

    private final String TAG = "AutoUpdateService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e(TAG, "onStartCommand");
        if (!intent.getBooleanExtra("is_first", true)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateWeather();
                }
            }).start();
        }
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 2 * 60 * 60 * 1000;//2小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(getApplicationContext(), AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return START_REDELIVER_INTENT;
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = preferences.getString("weather_code_1", "");
        LogUtil.e(TAG, "weatherCode =" + weatherCode);
        String address = Prams.QUERY_WEATHER + weatherCode + Prams.HTML;
        LogUtil.url(address);
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                LogUtil.response(response);
                Utility.handleWeatherResponse(getApplicationContext(), response);
                showWeather();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并更新通知天气。
     */
    private void showWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String temp1, temp2, weatherDesp;
        temp1 = preferences.getString("temp2", "").replaceAll("℃", "");
        temp2 = preferences.getString("temp1", "").replaceAll("℃", "");
        weatherDesp = preferences.getString("weather_desp", "");
        int weatherImgRes = R.drawable.sun_1;
        if (weatherDesp.contains("晴")) {
            weatherImgRes = R.drawable.sun_1;
        } else if (weatherDesp.contains("阴")) {
            weatherImgRes = R.drawable.yin;
        } else if (weatherDesp.contains("多云")) {
            weatherImgRes = R.drawable.duo_yun;
        } else if (weatherDesp.contains("雨")) {
            weatherImgRes = R.drawable.yu;
        } else if (weatherDesp.contains("雪")) {
            weatherImgRes = R.drawable.xue;
        }
        Utility.sendNotification(getApplicationContext(), weatherImgRes, (Integer.valueOf(temp2) - 3) + "℃",
                temp1 + "~" + temp2 + "℃", weatherDesp, preferences.getString("city_name", ""),
                preferences.getString("publish_time", ""));
    }
}
