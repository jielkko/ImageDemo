package com.hjl.imagedemo;

import android.app.Application;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.xutils.image.ImageOptions;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ContextUtil.init(this);
    }
}