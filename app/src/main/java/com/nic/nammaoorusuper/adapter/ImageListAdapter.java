package com.nic.nammaoorusuper.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.activity.PhotoCategoryList;
import com.nic.nammaoorusuper.databinding.GalleryThumbnailBinding;
import com.nic.nammaoorusuper.model.NOS;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.utils.Utils;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MyViewHolder> {

    private Activity context;
    private PrefManager prefManager;
    private List<NOS> imageList;
    com.nic.nammaoorusuper.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;
    int pos=-1;

    public ImageListAdapter(Activity context,List<NOS> imageList, com.nic.nammaoorusuper.dataBase.dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.imageList = imageList;
    }

    @Override
    public ImageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        GalleryThumbnailBinding galleryThumbnailBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.gallery_thumbnail, viewGroup, false);
        return new MyViewHolder(galleryThumbnailBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private GalleryThumbnailBinding galleryThumbnailBinding;

        public MyViewHolder(GalleryThumbnailBinding Binding) {
            super(Binding.getRoot());
            galleryThumbnailBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ImageListAdapter.MyViewHolder holder, final int position) {

        Glide.with(context).load(imageList.get(position).getImage())
                .thumbnail(0.5f)
                .into(holder.galleryThumbnailBinding.thumbnail);

        holder.galleryThumbnailBinding.description.setText(imageList.get(position).getDescription());
    }




    @Override
    public int getItemCount() {
        return imageList.size();
    }




}

