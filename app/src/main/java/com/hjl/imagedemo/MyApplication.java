package com.hjl.imagedemo;

import android.app.Application;

import com.hjl.imageselector.common.ImageContextUtil;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ContextUtil.init(this);
        ImageContextUtil.init(this);
    }
}