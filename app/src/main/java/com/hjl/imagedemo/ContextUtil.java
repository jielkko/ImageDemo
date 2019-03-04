package com.hjl.imagedemo;

import android.content.Context;

/**
 * Created by Administrator on 2018/4/17.
 */

public class ContextUtil {
    private static Context context;
    public static void init(Context ctx){
        context = ctx;
    }
    public static Context getContext(){
        return context;
    }
}
