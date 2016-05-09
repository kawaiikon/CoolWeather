package com.coolweather.coolweather.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.coolweather.coolweather.util.MyActivityManager;

/**
 * Created by bian on 2016/5/9 10:58.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.addActivity(this);
    }
}
