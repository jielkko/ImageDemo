package com.hjl.imageselector.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.hjl.imageselector.ImagePicker;
import com.hjl.imageselector.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class ImageBaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static String TAG = "ImageBaseActivity";

    protected static final int RC_CAMERA_AND_STORAGE = 6601; //获取权限
    protected static final int RC_TACKPICTURE = 6602;     //拍照
    /**
     * context
     */
    protected Context mContext;


    /**
     * 设置布局
     *
     * @return
     */
    public abstract int intiLayout();

    /**
     * 初始化界面
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 绑定事件
     */
    protected abstract void setEvent();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局
        setContentView(intiLayout());
        initView();
        setEvent();
        mContext = this;
        checkPermission();
    }



    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            String[] perms = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(this, perms)) {
                initData();
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(this, "部分功能需要获取存储空间；否则，您将无法正常使用",
                        RC_CAMERA_AND_STORAGE, perms);
            }


        } else {
            initData();
        }
    }




    //同意授权
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
        Log.i(TAG, "onPermissionsGranted:" + requestCode + ":" + list.size());
        initData();


    }


    /**
     * 请求权限失败
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // ToastUtils.showToast(getApplicationContext(), "用户授权失败");
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    protected static File takeImageFile;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RC_TACKPICTURE:
                if ( resultCode == RESULT_OK) {
                    Log.d(TAG, "返回选择结果: "+data);

                    //获取Options对象
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //仅做解码处理，不加载到内存
                    options.inJustDecodeBounds = true;
                    //解析文件
                    BitmapFactory.decodeFile(takeImageFile.getPath(), options);
                     //获取宽高
                    int imgWidth = options.outWidth;
                    int imgHeight = options.outHeight;

                    ArrayList<ImageItem> imageList = new ArrayList<>();
                    ImageItem imageItem = new ImageItem();
                    imageItem.path = takeImageFile.getPath();
                    imageItem.width = imgWidth;
                    imageItem.height = imgHeight;
                    imageList.add(imageItem);

                /*    if (imagePicker.isCrop()) {
                        Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                        startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                        finish();
                    }*/
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imageList);
                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                    finish();
                }else{
                    ArrayList<ImageItem> imageList = new ArrayList<>();
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imageList);
                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                    finish();
                    finish();
                }

                break;
        }
    }
}
