package com.hjl.imageselector.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjl.imageselector.ImagePicker;
import com.hjl.imageselector.R;
import com.hjl.imageselector.adapter.ImagePageAdapter;
import com.hjl.imageselector.bean.ImageItem;
import com.hjl.imageselector.view.SystemBarTintManager;
import com.hjl.imageselector.view.ViewPagerFixed;

import java.util.ArrayList;

public class ImagePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    protected SystemBarTintManager tintManager;

    protected ImagePicker imagePicker;
    protected ArrayList<ImageItem> mImageItems;      //跳转进ImagePreviewFragment的图片文件夹
    protected int mCurrentPosition = 0;              //跳转进ImagePreviewFragment时的序号，第几个图片
    protected TextView mTitleCount;                  //显示当前图片的位置  例如  5/31
    protected ArrayList<ImageItem> selectedImages;   //所有已经选中的图片
    protected ImagePageAdapter mAdapter;

    private int selectLimit = ImagePicker.getInstance().getSelectLimit();

    private RelativeLayout mContent;
    private ViewPagerFixed mViewpager;
    private ImageView mBtnBack;
    private TextView mTvDes;
    private Button mBtnOk;
    private AppCompatImageView mBtnDel;
    private LinearLayout mBottomBar;
    private ImageView mCbCheck;
    private View mMarginBottom;


    protected View topBar;


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.ip_color_primary_dark);  //设置上方状态栏的颜色


        Intent data = getIntent();

        if (data != null && data.getExtras() != null) {
            mImageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImageGridActivity.EXTRAS_IMAGES);
            //mImageItems.remove(0);
            mCurrentPosition = (int) data.getSerializableExtra(ImageGridActivity.EXTRAS_TAKE_POSITION);
            //mCurrentPosition--;

            if ((ArrayList<ImageItem>) data.getSerializableExtra(ImageGridActivity.EXTRAS_TAKE_SELECTED) != null) {
                selectedImages = (ArrayList<ImageItem>) data.getSerializableExtra(ImageGridActivity.EXTRAS_TAKE_SELECTED);
            }
        }

        initFindById();
        setEvent();

        mTvDes.setText((mCurrentPosition+1)+"/"+mImageItems.size());
        mBtnOk.setText(getString(R.string.ip_select_complete, selectedImages.size(), ImagePicker.getInstance().getSelectLimit()));

        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });


        isSelected(mImageItems.get(mCurrentPosition));


        mViewpager.setAdapter(mAdapter);
        mViewpager.setCurrentItem(mCurrentPosition, false);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                mTvDes.setText((position+1)+"/"+mImageItems.size());
                isSelected(mImageItems.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void isSelected(ImageItem pItem){
        Resources resources = mContent.getResources();
        if(selectedImages.contains(pItem)){
            mCbCheck.setImageDrawable(resources.getDrawable(R.drawable.image_check_blue1));
        }else{
            mCbCheck.setImageDrawable(resources.getDrawable(R.drawable.image_check_blue0));

        }

    }
    private void isClickSelected(ImageItem pItem){
        Resources resources = mContent.getResources();
        if(selectedImages.contains(pItem)){
            mCbCheck.setImageDrawable(resources.getDrawable(R.drawable.image_check_blue0));
            selectedImages.remove(pItem);
        }else{
            if(selectedImages.size()<selectLimit){
                mCbCheck.setImageDrawable(resources.getDrawable(R.drawable.image_check_blue1));
                selectedImages.add(pItem);
            }
        }
        mBtnOk.setText(getString(R.string.ip_select_complete, selectedImages.size(), ImagePicker.getInstance().getSelectLimit()));

    }

    private void initFindById(){
        mContent = (RelativeLayout) findViewById(R.id.content);
        mViewpager = (ViewPagerFixed) findViewById(R.id.viewpager);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mTvDes = (TextView) findViewById(R.id.tv_des);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnDel = (AppCompatImageView) findViewById(R.id.btn_del);
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        mCbCheck = (ImageView) findViewById(R.id.cb_check);
        mMarginBottom = (View) findViewById(R.id.margin_bottom);

        topBar = findViewById(R.id.top_bar);
    }

    private void setEvent() {
        mBtnOk.setOnClickListener(this);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, selectedImages);
                setResult(ImagePicker.RESULT_CODE_BACK, intent);
                finish();
            }
        });


        mBottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isClickSelected(mImageItems.get(mCurrentPosition));

            }
        });
    }


    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
            tintManager.setStatusBarTintResource(Color.TRANSPARENT);//通知栏所需颜色
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
//            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
            tintManager.setStatusBarTintResource(R.color.ip_color_primary_dark);//通知栏所需颜色
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
//            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, selectedImages);
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            finish();
        } else if (id == R.id.btn_back) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, selectedImages);
            setResult(ImagePicker.RESULT_CODE_BACK, intent);
            finish();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, selectedImages);
            setResult(ImagePicker.RESULT_CODE_BACK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
