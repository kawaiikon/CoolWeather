package com.coolweather.coolweather.util;

/**
 * Created on 2016/2/4.
 */
public interface HttpCallbackListener {

    //成功的回调方法
    void onFinish(String response);

    //失败的回调方法
    void onError(Exception e);
}
