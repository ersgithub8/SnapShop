package com.fyp.snapshop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.snapshop.Adapters.GalleryAdapter;
import com.fyp.snapshop.Models.GalleryModel;
import com.fyp.snapshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.VISIBLE;

public class ImagePreviewActivity extends AppCompatActivity {

    GalleryAdapter galleryAdapter;
    List<GalleryModel> list;
    private ImageView iv_Preview_Pics;
    private Button btn_Del_Pic, download;
    int code;
    String id,id2;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        list=new ArrayList<>();
        galleryAdapter=new GalleryAdapter(list,this);
        bindViews();
        listeners();

        btn_Del_Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dellimage(galleryAdapter.getItemCount());
            }
        });

        code = getIntent().getExtras().getInt("CODE");
        id = getIntent().getExtras().getString("IMGURL");
        id2 = getIntent().getExtras().getString("IMGID");

        if(code == 0)
        {
            btn_Del_Pic.setVisibility(View.INVISIBLE);
            Glide.with(ImagePreviewActivity.this).load(id).into(iv_Preview_Pics);
        }
        else if(code == 1)
        {
            btn_Del_Pic.setVisibility(VISIBLE);
            Glide.with(ImagePreviewActivity.this).load(id2).into(iv_Preview_Pics);
        }

        //Glide.with(ImagePreviewActivity.this).load(id).into(iv_Preview_Pics);

    }

    private void bindViews() {
        iv_Preview_Pics = findViewById(R.id.iv_pic_preview);
        download = findViewById(R.id.download);
        btn_Del_Pic = findViewById(R.id.btn_del_pic);
    }

    private void listeners() {

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Picasso.get()
                        .load(id)
                        .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        try {
                            String root = Environment.getExternalStorageDirectory().toString();
                            File myDir = new File(root + "/yourDirectory");

                            if (!myDir.exists()) {
                                myDir.mkdirs();
                            }

                            String name = new Date().toString() + ".jpg";
                            myDir = new File(myDir, name);
                            FileOutputStream out = new FileOutputStream(myDir);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                            out.flush();
                            out.close();
                            Toast.makeText(ImagePreviewActivity.this, "ok", Toast.LENGTH_SHORT).show();
                        } catch(Exception e){
                            Toast.makeText(ImagePreviewActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                            Log.e("ImageError", e.toString());
                            // some action
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(ImagePreviewActivity.this, "Failed To Download", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        });
    }
    public void dellimage(int position){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("editor").child("gallery");

        GalleryModel selectedItem = list.get(position);
        final String selectedKey = selectedItem.getmKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getGalleryImage());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(ImagePreviewActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
        galleryAdapter.notifyDataSetChanged();

    }

}
