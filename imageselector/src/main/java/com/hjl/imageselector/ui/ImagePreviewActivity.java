package com.hjl.imageselector.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjl.imageselector.R;
import com.hjl.imageselector.view.ViewPagerFixed;

public class ImagePreviewActivity extends AppCompatActivity {


    private RelativeLayout mContent;
    private ViewPagerFixed mViewpager;
    private ImageView mBtnBack;
    private TextView mTvDes;
    private Button mBtnOk;
    private AppCompatImageView mBtnDel;
    private LinearLayout mBottomBar;
    private ImageView mCbCheck;
    private View mMarginBottom;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);


        mContent = (RelativeLayout) findViewById(R.id.content);
        mViewpager = (ViewPagerFixed) findViewById(R.id.viewpager);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mTvDes = (TextView) findViewById(R.id.tv_des);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnDel = (AppCompatImageView) findViewById(R.id.btn_del);
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        mCbCheck = (ImageView) findViewById(R.id.cb_check);
        mMarginBottom = (View) findViewById(R.id.margin_bottom);
    }
}
