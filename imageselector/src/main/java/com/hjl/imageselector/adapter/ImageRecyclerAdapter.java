package com.hjl.imageselector.adapter;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.hjl.imageselector.ImagePicker;
import com.hjl.imageselector.R;
import com.hjl.imageselector.bean.ImageItem;
import com.hjl.imageselector.common.ImageContextUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ImageRecyclerAdapter extends RecyclerView.Adapter {

    private static final String TAG = "ImageRecyclerAdapter";  //第一个条目是相机
    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    private Activity mActivity;
    private List<ImageItem> mData;
    private ArrayList<ImageItem> mSelectedImages = new ArrayList<>(); //全局保存的已经选中的图片数据
    private int selectLimit = ImagePicker.getInstance().getSelectLimit();
    private boolean isShowCamera = true;         //是否显示拍照按钮


    //HeaderView, FooterView
    private View mHeaderView;
    private View mFooterView;

    public ImageRecyclerAdapter(Activity mActivity, List<ImageItem> data, ArrayList<ImageItem> selectedImages) {
        this.mActivity = mActivity;
        this.mData = data;
        this.mSelectedImages = selectedImages;
        //mData.add(0, new ImageItem());
    }

    private void setSelect() {

        for (int i = 0; i < mData.size(); i++) {
            if (mSelectedImages.contains(mData.get(i))) {
                mData.get(i).isSelected = 1;
            } else {
                mData.get(i).isSelected = 0;
            }
        }

    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images != null) {
            mData = images;
        }
        setSelect();
        notifyDataSetChanged();
    }


    public void setAllImages(List<ImageItem> selectedPhotos) {
        if (selectedPhotos != null) {
            mData = selectedPhotos;
        }
        setSelect();
        //notifyDataSetChanged();
    }

    public void setSelectedImages(ArrayList<ImageItem> selectedPhotos) {
        mSelectedImages = selectedPhotos;
        setSelect();
        notifyDataSetChanged();
    }

    //HeaderView和FooterView的get和set函数
    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * 获取选中图片数量
     *
     * @return
     */
    public int getmSelectedImagesNum() {
        return mSelectedImages.size();
    }

    /**
     * 获取选中图片
     *
     * @return
     */
    public ArrayList<ImageItem> getmSelectedImages() {
        return mSelectedImages;
    }

    @Override
    public int getItemViewType(int position) {

        if (mHeaderView == null && mFooterView == null) {
            return ITEM_TYPE_NORMAL;
        }
        if (position == 0) {
            //第一个item应该加载Header
            return ITEM_TYPE_CAMERA;
        }
        /*if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return ITEM_TYPE_CAMERA;
        }*/
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView != null) {
            //加了头布局,多一个
            return mData.size() + 1;
        }

        return mData.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mHeaderView != null && viewType == ITEM_TYPE_CAMERA) {
            return new CameraViewHolder(mHeaderView);
        }
   /*     if(mFooterView != null && viewType == TYPE_FOOTER){
            return new ItemFootViewHolder(mFooterView);
        }*/
        if (viewType == ITEM_TYPE_NORMAL) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.adapter_image_list_item, viewGroup, false);
            return new ItemViewHolder(view);
        }
        View view = LayoutInflater.from(mActivity).inflate(R.layout.adapter_image_list_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);


        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

            ImageView imageView = itemViewHolder.mIcon;
            if (imageView != null) {
                Glide.with(mActivity).clear(imageView);
            }
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

            int itemPosition = i;
            if (mHeaderView != null) {
                itemPosition = i - 1;
            }
            ImageItem item = mData.get(itemPosition);
            Log.d(TAG, "item.path= " + item.path);
            if (item.path != null) {
                Glide.with(mActivity)
                        .load(new File(item.path))
                        .apply(new RequestOptions()
                                //.skipMemoryCache(true)
                                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(200, 200)
                                .centerCrop()
                        )
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                itemViewHolder.mIcon.setImageDrawable(resource);
                            }
                        });
            } else {
                Glide.with(mActivity)
                        .load(R.drawable.ic_default_image)
                        .apply(new RequestOptions()
                                //.skipMemoryCache(true)
                                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .override(200, 200)
                                .error(R.drawable.ic_default_image)
                                .centerCrop()
                        )
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                itemViewHolder.mIcon.setImageDrawable(resource);
                            }
                        });
            }



      /*      Glide.with(mActivity)
                    .load(new File(item.path))
                    .apply(new RequestOptions()
                            .skipMemoryCache(true)//跳过内存缓存。
                            //.skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(200, 200)
                            .centerCrop()
                    )
                    .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                    .thumbnail(0.1f)
                    .into(itemViewHolder.mIcon);*/
         /*   Glide.with(mActivity)
                    .load(new File(item.path))
                    .apply(new RequestOptions()
                            //.skipMemoryCache(true)
                            //.diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(200, 200)
                            .centerCrop()
                    )
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            itemViewHolder.mIcon.setImageDrawable(resource);
                        }
                    });*/

            itemViewHolder.mIsChooseIcon.setVisibility(ImagePicker.getInstance().isMultiMode() ? View.VISIBLE : View.GONE);

            if (item.isSelected == 0) {
                //未选中
                itemViewHolder.mIsChooseIcon.setImageDrawable(itemViewHolder.mIcCheckBoxNormal);
                itemViewHolder.mMask.setVisibility(View.GONE);
            } else {
                //已选中
                itemViewHolder.mIsChooseIcon.setImageDrawable(itemViewHolder.mIcCheckBoxChecked);
                itemViewHolder.mMask.setVisibility(View.VISIBLE);

            }
            itemViewHolder.mIsChooseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutPosition = itemViewHolder.getLayoutPosition();
                    if (mHeaderView != null) {
                        layoutPosition = itemViewHolder.getLayoutPosition() - 1;
                    }
                    //Toast.makeText(mContext, "点击" + layoutPosition, Toast.LENGTH_SHORT).show();
                    if (mData.get(layoutPosition).isSelected == 0) {
                        //未选中
                        if (mSelectedImages.size() < selectLimit) {
                            mSelectedImages.add(mData.get(layoutPosition));
                            mData.get(layoutPosition).isSelected = 1;
                        } else {
                            Toast.makeText(mActivity, "您最多只能选择" + selectLimit + "张照片", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //已选中
                        mSelectedImages.remove(mData.get(layoutPosition));
                        mData.get(layoutPosition).isSelected = 0;
                    }
                    notifyItemChanged(itemViewHolder.getLayoutPosition());

                    if (mOnItemSelectClickListener != null) {
                        mOnItemSelectClickListener.onItemClick(v, layoutPosition);
                    }

                }
            });


            itemViewHolder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutPosition = itemViewHolder.getLayoutPosition();

                    /*if(!ImagePicker.getInstance().isMultiMode()){
                        //单选
                        mSelectedImages.clear();
                        mSelectedImages.add(mData.get(layoutPosition));
                        mData.get(layoutPosition).isSelected = 1;

                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS,mSelectedImages);
                        mActivity.setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                        mActivity.finish();

                    }else{
                        if (mOnItemClickListener != null) {
                            int position = itemViewHolder.getLayoutPosition();
                            mOnItemClickListener.onItemClick(v, position);
                        }
                    }*/
                    if (mOnItemClickListener != null) {
                        int position = itemViewHolder.getLayoutPosition();
                        if (mHeaderView != null) {
                            position = itemViewHolder.getLayoutPosition() - 1;
                        }
                        mOnItemClickListener.onItemClick(v, position);
                    }


                }
            });


        }

        if (viewHolder instanceof CameraViewHolder) {
            final CameraViewHolder itemViewHolder = (CameraViewHolder) viewHolder;
            itemViewHolder.mCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnTaskCreamPictureClickListener != null) {
                        int position = itemViewHolder.getLayoutPosition();
                        mOnTaskCreamPictureClickListener.onItemClick(v, position);
                    }
                }
            });

        }

    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mContainer;
        private ImageView mIcon;
        private View mMask;
        private LinearLayout mIsChooseBtn;
        private ImageView mIsChooseIcon;


        //图标素材
        private Drawable mIcCheckBoxChecked;
        private Drawable mIcCheckBoxNormal;


        public ItemViewHolder(View view) {
            super(view);

            mContainer = (LinearLayout) view.findViewById(R.id.container);
            mIcon = (ImageView) view.findViewById(R.id.icon);
            mMask = (View) view.findViewById(R.id.mask);
            mIsChooseBtn = (LinearLayout) view.findViewById(R.id.isChoose_btn);
            mIsChooseIcon = (ImageView) view.findViewById(R.id.isChoose_icon);

            Resources resources = view.getResources();
            mIcCheckBoxChecked = resources.getDrawable(R.drawable.image_check_blue1);
            mIcCheckBoxNormal = resources.getDrawable(R.drawable.image_check_blue0);
        }
    }


    static class CameraViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout mCamera;

        public CameraViewHolder(View view) {
            super(view);
            mCamera = (RelativeLayout) view.findViewById(R.id.camera);
        }
    }

    //拍照
    private OnTaskCreamPictureClickListener mOnTaskCreamPictureClickListener;

    public interface OnTaskCreamPictureClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnTaskCreamPictureClickListener(OnTaskCreamPictureClickListener mOnTaskCreamPictureClickListener) {
        this.mOnTaskCreamPictureClickListener = mOnTaskCreamPictureClickListener;
    }

    //点击选择框
    private OnItemSelectClickListener mOnItemSelectClickListener;

    public interface OnItemSelectClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemSelectClickListener(OnItemSelectClickListener mOnItemSelectClickListener) {
        this.mOnItemSelectClickListener = mOnItemSelectClickListener;
    }

    //点击
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //长按
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
}
