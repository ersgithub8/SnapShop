package com.fyp.snapshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.snapshop.Models.GalleryModel;
import com.fyp.snapshop.Models.Model;
import com.fyp.snapshop.R;
import com.fyp.snapshop.activities.ImagePreviewActivity;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.viewholder>{
    List<GalleryModel> gallery;
    Context context;
    OnClickListner listner;

    public GalleryAdapter(List<GalleryModel> gallery, Context context) {
        this.gallery = gallery;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.editor_galary_item,null,false);
        viewholder viewholder =  new viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, final int position) {
        final GalleryModel gm = gallery.get(position);
        Glide.with(context).load(gm.getGalleryImage()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClick(position , gm);

            }
        });





    }

    @Override
    public int getItemCount() {
        return gallery.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView2);
        }
    }

    public interface OnClickListner{

        public void onClick(int position, GalleryModel gm);
        //ModelShowTutor modelShowTutor it is model
    }


    public void setOnClickListener(OnClickListner listner){
        this.listner = listner;
    }

}
