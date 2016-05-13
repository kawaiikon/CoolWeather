package com.coolweather.coolweather.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bian on 2016/5/9 10:59.
 */
public class MyActivityManager {

    private static List<Activity> mList = new ArrayList<>();

    /**
     * 把打开的activity存到管理列表中
     * @param activity 打开的activity
     */
    public static void addActivity(Activity activity){
        mList.add(activity);
    }

    /**
     * 关闭所有打卡的activity
     */
    public static void finishAllActivity(){
        for (Activity activity : mList){
            activity.finish();
        }
    }
}
