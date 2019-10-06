package com.hjl.imageselector.common;

import android.content.Context;

public class ImageContextUtil {
    private static Context context;

    public static void init(Context ctx) {
        context = ctx;
    }

    public static Context getContext() {
        return context;
    }
}
