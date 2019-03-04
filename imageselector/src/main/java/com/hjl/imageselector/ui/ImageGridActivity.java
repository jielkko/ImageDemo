package com.hjl.imageselector.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjl.imageselector.ImagePicker;
import com.hjl.imageselector.R;
import com.hjl.imageselector.adapter.ImageRecyclerAdapter;
import com.hjl.imageselector.bean.ImageItem;
import com.hjl.imageselector.util.ProviderUtil;
import com.hjl.imageselector.util.Utils;
import com.hjl.imageselector.view.GridSpacingItemDecoration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

/**
 * 图片选择
 */
public class ImageGridActivity extends ImageBaseActivity {

    private static String TAG = "ImageGridActivity";




    private ImageView mBtnBack;
    private TextView mTvDes;
    private Button mBtnOk;
    private AppCompatImageView mBtnDel;
    private RecyclerView mRecyclerView;
    private RelativeLayout mFooterBar;
    private RelativeLayout mLlDir;
    private TextView mTvDir;
    private TextView mBtnPreview;


    private LinearLayoutManager mLayoutManager;
    public List<ImageItem> mData = new ArrayList<>();
    public ImageRecyclerAdapter mAdapter;

    public List<ImageItem> mAllData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int intiLayout() {
        //设置子类的布局
        return R.layout.activity_image_grid;
    }

    protected void initView() {

        mContext = ImageGridActivity.this;

        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mTvDes = (TextView) findViewById(R.id.tv_des);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnDel = (AppCompatImageView) findViewById(R.id.btn_del);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mFooterBar = (RelativeLayout) findViewById(R.id.footer_bar);
        mLlDir = (RelativeLayout) findViewById(R.id.ll_dir);
        mTvDir = (TextView) findViewById(R.id.tv_dir);
        mBtnPreview = (TextView) findViewById(R.id.btn_preview);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, Utils.dp2px(this, 2), false));

        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mAdapter = new ImageRecyclerAdapter(mContext, mData);
        mRecyclerView.setAdapter(mAdapter);

        //选择
        mAdapter.setOnItemSelectClickListener(new ImageRecyclerAdapter.OnItemSelectClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mAdapter.getmSelectedImagesNum()>0){
                    mBtnOk.setText(getString(R.string.ip_select_complete, mAdapter.getmSelectedImagesNum(), 9));
                    mBtnOk.setEnabled(true);
                    mBtnPreview.setEnabled(true);
                    mBtnPreview.setText(getResources().getString(R.string.ip_preview_count, 9));
                    mBtnOk.setTextColor(mContext.getResources().getColor(R.color.ip_text_primary_inverted));
                    mBtnPreview.setTextColor(mContext.getResources().getColor(R.color.ip_text_primary_inverted));
                }else{
                    mBtnOk.setText(getString(R.string.ip_complete));
                    mBtnOk.setEnabled(false);
                    mBtnPreview.setEnabled(false);
                    mBtnPreview.setText(getResources().getString(R.string.ip_preview));
                    mBtnPreview.setTextColor(mContext.getResources().getColor(R.color.ip_text_secondary_inverted));
                    mBtnPreview.setTextColor(mContext.getResources().getColor(R.color.ip_text_secondary_inverted));
                }


                //mBtnOk.setText("完成("+mAdapter.getmSelectedImagesNum()+"/9）");
                //mBtnPreview.setText("预览("+mAdapter.getmSelectedImagesNum()+"）");
            }
        });
        //预览
        mAdapter.setOnItemClickListener(new ImageRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                //ToastUtil.showShort(mContext, "点击条目上的按钮" + position);
                Intent intent = new Intent();
                intent.setClass(ImageGridActivity.this, ImagePreviewActivity.class);
                //intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
                startActivityForResult(intent, 200);

            }
        });
        mAdapter.setOnTaskCreamPictureClickListener(new ImageRecyclerAdapter.OnTaskCreamPictureClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                takePicture(ImageGridActivity.this,RC_TACKPICTURE);
            }
        });

    }

    @Override
    protected void setEvent() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter.getmSelectedImagesNum()>0){
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mAdapter.getmSelectedImages());
                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                    finish();
                }
            }
        });
    }

    protected void initData(){
        initImage();

        mBtnOk.setText(getString(R.string.ip_complete));
        mBtnOk.setEnabled(false);
        mBtnPreview.setEnabled(false);
        mBtnPreview.setText(getResources().getString(R.string.ip_preview));
        mBtnPreview.setTextColor(mContext.getResources().getColor(R.color.ip_text_secondary_inverted));
        mBtnPreview.setTextColor(mContext.getResources().getColor(R.color.ip_text_secondary_inverted));


    }



    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //mAdapter.setSelectedPhotos(mData);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    public static final int LOADER_ALL = 0;         //加载所有图片
    public static final int LOADER_CATEGORY = 1;    //分类加载图片
    private final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED};    //图片被添加的时间，long型  1450518608

    private void initImage() {
        //由于扫描图片是耗时的操作，所以要在子线程处理。
        new Thread(new Runnable() {
            @Override
            public void run() {
                //扫描图片
                //CursorLoader cursor = null;
                //扫描所有图片
                //if (id == LOADER_ALL)
                //cursor = new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[6] + " DESC");
                //扫描某个图片文件夹
                //if (id == LOADER_CATEGORY)
                //cursor = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[6] + " DESC");
                mData.clear();

                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, IMAGE_PROJECTION[6] + " DESC");

                //一个辅助集合，防止同一目录被扫描多次
                HashSet<String> dirPaths = new HashSet<String>();

                while (cursor.moveToNext()) {


                    // 获取图片的路径
                    //查询数据
                    String imageName = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));

                    File file = new File(imagePath);
                    if (!file.exists() || file.length() <= 0) {
                        continue;
                    }
                    //获取图片的生成日期
                    long imageSize = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                    int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                    String imageMimeType = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                    //Long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));

                    Long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));


                    //Log.d("路径", "get: " + imagePath);
                    //Log.d(TAG, "获取图片的生成日期: "+imageAddTime);
                    //Log.d(TAG, "获取图片的生成日期: "+timeStampToDateString(imageAddTime));

                    //封装实体
                    ImageItem imageItem = new ImageItem();
                    imageItem.name = imageName;
                    imageItem.path = imagePath;
                    imageItem.size = imageSize;
                    imageItem.width = imageWidth;
                    imageItem.height = imageHeight;
                    imageItem.mimeType = imageMimeType;
                    imageItem.addTime = imageAddTime;
                    mData.add(imageItem);

                }
                Log.d(TAG, "initData: " + mData.size());

                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                cursor.close();

            }
        }).start();

    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String timeStampToDateString(long timeStamp) {
        //long timeStamp = 1495777335060;//直接是时间戳
        //long timeStamp = System.currentTimeMillis();  //获取当前时间戳,也可以是你自已给的一个随机的或是别人给你的时间戳(一定是long型的数据)
        String dStr = dateFormat.format(new Date(timeStamp));   // 时间戳转换成时间
        //System.out.println(dStr);//打印出你要的时间
        //结果就是: 2017-05-26 13:42:15
        return dStr;
    }




    /**
     * 拍照的方法
     */
    public void takePicture(Activity activity, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (Utils.existSDCard()) {
                takeImageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            }else{
                takeImageFile = Environment.getDataDirectory();
            }
            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg");
            if (takeImageFile != null) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！

                Uri uri;
                if (VERSION.SDK_INT <= VERSION_CODES.M) {
                    uri = Uri.fromFile(takeImageFile);
                } else {

                    /**
                     * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
                     * 并且这样可以解决MIUI系统上拍照返回size为0的情况
                     */
                    uri = FileProvider.getUriForFile(activity, ProviderUtil.getFileProviderName(activity), takeImageFile);
                    //加入uri权限 要不三星手机不能拍照
                    List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                Log.e("nanchen", ProviderUtil.getFileProviderName(activity));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }
        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }


}

