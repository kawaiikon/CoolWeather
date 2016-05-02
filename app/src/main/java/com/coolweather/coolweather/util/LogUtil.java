package com.coolweather.coolweather.util;

import android.util.Log;

/**
 * Created on 2016/4/4.
 */
public class LogUtil {

//    private static final int VERBOSE = 1;
//    private static final int DEBUG = 2;
//    private static final int INFO = 3;
//    private static final int WARN = 4;
//    private static final int ERROR = 5;
//    private static final int NOTHING = 6;

//    private static final int LEVEL = VERBOSE;

    private static boolean TAG = true;

    public static void v(String tag, String msg){
        if (TAG){
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg){
        if (TAG){
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg){
        if (TAG){
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg){
        if (TAG){
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if (TAG){
            Log.e(tag, msg);
        }
    }

    public static void url(String url){
        e("url", url);
    }

    public static void response(String response){
        e("response", response);
    }
}
