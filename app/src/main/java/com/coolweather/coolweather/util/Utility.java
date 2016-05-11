package com.coolweather.coolweather.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.coolweather.coolweather.CommonRes;
import com.coolweather.coolweather.R;
import com.coolweather.coolweather.activity.MainActivity;
import com.coolweather.coolweather.model.AddedCity;
import com.coolweather.coolweather.model.City;
import com.coolweather.coolweather.model.CoolWeatherDB;
import com.coolweather.coolweather.model.County;
import com.coolweather.coolweather.model.Province;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created on 2016/2/4.
 */
public class Utility {

    /*
    * 解析和处理服务器返回的省级数据
    * */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!response.isEmpty()) {
            String[] allProvince = response.split(",");
            if (allProvince != null && allProvince.length > 0) {
                for (String p : allProvince) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的市级数据
    * */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!response.isEmpty()) {
            String[] allCity = response.split(",");
            if (allCity != null && allCity.length > 0) {
                for (String p : allCity) {
                    String[] array = p.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到Province表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /*
    * 解析和处理服务器返回的县级数据
    * */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!response.isEmpty()) {
            String[] allCounty = response.split(",");
            if (allCounty != null && allCounty.length > 0) {
                for (String p : allCounty) {
                    String[] array = p.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /*
    * 解析服务器返回的Json数据，并将解析出的数据存储到本的
    * */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatrherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatrherInfo.getString("city");
            String weatherCode = weatrherInfo.getString("cityid");
            String temp1 = weatrherInfo.getString("temp1");
            String temp2 = weatrherInfo.getString("temp2");
            String weatherDesp = weatrherInfo.getString("weather");
            String publishTime = weatrherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    * 将服务器返回的所有天气信息存储到SharedPreferences文件中
    * */
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1,
                                       String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_data", format.format(new Date()));
        editor.commit();
    }

    /**
     * 把已选择的城市存到本地
     *
     * @param context 上下文
     * @param list    已选择的城市
     */
    public static void saveAddedCity(Context context, List<AddedCity> list) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CommonRes.ADDED_CITY_LIST, new Gson().toJson(list));
        editor.commit();
    }

    /**
     * 读取存到本地的已选择的城市
     *
     * @param context 上下文
     * @return 已选择的城市
     */
    public static List<AddedCity> loadAddedCity(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getString(CommonRes.ADDED_CITY_LIST, "").equals("")) {
            Type listType = new TypeToken<ArrayList<AddedCity>>() {
            }.getType();
            return new Gson().fromJson(preferences.getString(CommonRes.ADDED_CITY_LIST, ""), listType);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 用于判断时间差是否大于30秒
     * @param calendar1 Calender 对象
     * @param calendar2 Calender 对象
     * @return true 时间差大于30秒
     */
    public static Boolean compare(Calendar calendar1, Calendar calendar2){
        LogUtil.e("calender1", "" + calendar1.getTimeInMillis());
        LogUtil.e("calender2", "" + calendar2.getTimeInMillis());
        if (calendar1.getTimeInMillis() - calendar2.getTimeInMillis() >= 30000 ||
                calendar1.getTimeInMillis() - calendar2.getTimeInMillis() <= -30000){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 发送天气notification
     * @param context 上下文
     * @param weatherImgRes 天气图标资源id
     * @param currentTemp 当前气温
     * @param temp 气温
     * @param weather 天气
     * @param city 城市
     * @param sendTime 发布时间
     */
    public static void sendNotification(Context context, int weatherImgRes, String currentTemp, String temp,
                                        String weather, String city, String sendTime){
        RemoteViews contentViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);
        //通过控件的Id设置属性
        contentViews.setImageViewResource(R.id.weather_img, weatherImgRes);
        contentViews.setTextViewText(R.id.current_temp, currentTemp);
        contentViews.setTextViewText(R.id.temp_text, temp);
        contentViews.setTextViewText(R.id.weather_text, weather);
        contentViews.setTextViewText(R.id.city_text, city);
        contentViews.setTextViewText(R.id.send_time_text, sendTime);
        Intent intent = new Intent(context,
                MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(weatherImgRes);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContent(contentViews);
        mBuilder.setAutoCancel(true);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(10, notification);
    }
}
