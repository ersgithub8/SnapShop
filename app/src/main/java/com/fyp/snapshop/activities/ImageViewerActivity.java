package com.fyp.snapshop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fyp.snapshop.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ImageViewerActivity extends AppCompatActivity {

    ImageView download,image;
    String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        download = findViewById(R.id.downloadimage);
        image = findViewById(R.id.image);
        url = getIntent().getStringExtra("url");

        download.bringToFront();


        Picasso.get().load(url).into(image);


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.get()
                        .load(url)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                try {
                                    String root = Environment.getExternalStorageDirectory().toString();
                                    File myDir = new File(root + "/SnapShop");

                                    if (!myDir.exists()) {
                                        myDir.mkdirs();
                                    }

                                    String name = new Date().toString() + ".jpg";
                                    myDir = new File(myDir, name);
                                    FileOutputStream out = new FileOutputStream(myDir);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                                    out.flush();
                                    out.close();
                                    Toast.makeText(ImageViewerActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(ImageViewerActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                                    Log.e("ImageError", e.toString());
                                    // some action
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                Toast.makeText(ImageViewerActivity.this, "Failed To Download", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }

        });
    }
}