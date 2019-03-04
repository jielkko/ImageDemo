package com.hjl.imageselector.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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
import com.hjl.imageselector.R;
import com.hjl.imageselector.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageRecyclerAdapter extends RecyclerView.Adapter {

    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    private Context mContext;
    private List<ImageItem> mData;
    private ArrayList<ImageItem> mSelectedImages = new ArrayList<>(); //全局保存的已经选中的图片数据
    private int selectLimit = 9;
    private boolean isShowCamera = true;         //是否显示拍照按钮

    public ImageRecyclerAdapter(Context context, List<ImageItem> data) {
        this.mContext = context;
        this.mData = data;
        mData.add(0, new ImageItem());
    }

    public void setSelectedPhotos(List<ImageItem> selectedPhotos) {
        if (selectedPhotos != null) {
            mData = selectedPhotos;
            mData.add(0, new ImageItem());
        }
        notifyDataSetChanged();
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
        if (isShowCamera) return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == ITEM_TYPE_CAMERA) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_camera_item, viewGroup, false);
            return new CameraViewHolder(view);
        } else if (viewType == ITEM_TYPE_NORMAL) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_image_list_item, viewGroup, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_image_list_item, viewGroup, false);
            return new ItemViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ImageItem item = mData.get(i);
        if (viewHolder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;


            Glide.with(mContext)
                    .load(new File(item.path))
                    .apply(new RequestOptions()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(200, 200)
                            .centerCrop()
                    )
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            itemViewHolder.mIcon.setImageDrawable(resource);
                        }
                    });


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

                    Toast.makeText(mContext, "点击" + layoutPosition, Toast.LENGTH_SHORT).show();
                    if (mData.get(layoutPosition).isSelected == 0) {
                        //未选中
                        if (mSelectedImages.size() < selectLimit) {
                            mSelectedImages.add(mData.get(layoutPosition));
                            mData.get(layoutPosition).isSelected = 1;
                        }
                    } else {
                        //已选中
                        mSelectedImages.remove(mData.get(layoutPosition));
                        mData.get(layoutPosition).isSelected = 0;
                    }
                    notifyItemChanged(layoutPosition);

                    if (mOnItemSelectClickListener != null) {
                        mOnItemSelectClickListener.onItemClick(v, layoutPosition);
                    }

                }
            });


            itemViewHolder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        int position = itemViewHolder.getLayoutPosition();
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
    Activity mActivity;

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
