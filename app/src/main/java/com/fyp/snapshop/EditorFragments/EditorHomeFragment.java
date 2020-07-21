package com.fyp.snapshop.EditorFragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fyp.snapshop.Adapters.GalleryAdapter;
import com.fyp.snapshop.Models.GalleryModel;
import com.fyp.snapshop.R;
import com.fyp.snapshop.activities.ImagePreviewActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EditorHomeFragment extends Fragment {

    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<GalleryModel> list;
    private static final int Gallery_Req = 1;
    public Uri ImageUri;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //Realtime Database
    FirebaseDatabase database;
    DatabaseReference myRef,userGalaryRef,newRef;
    //Firebase Storage
    private StorageReference storageImage;
    String downloadUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        userGalaryRef = myRef.child("editor").child(user.getUid()).child( "gallery" );

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editor_home, container, false);
        init(view);
        listeners();

        userGalaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list = new ArrayList<>();
                for (final DataSnapshot userSnapShot : dataSnapshot.getChildren()){

                    GalleryModel gm = new GalleryModel();
                    gm.setGalleryImage(userSnapShot.getValue().toString());
                    list.add(gm);

                    galleryAdapter = new GalleryAdapter(list,getActivity());
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
                    recyclerView.setAdapter(galleryAdapter);


                    galleryAdapter.setOnClickListener(new GalleryAdapter.OnClickListner() {
                        @Override
                        public void onClick(int position, GalleryModel gm) {

                            startActivity(new Intent(getActivity(), ImagePreviewActivity.class).putExtra("CODE",1).putExtra("IMGID",gm.getGalleryImage()));
                        }
                    });
                }

               // galleryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }

    private void init(View view) {
        floatingActionButton = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.rv_galary);

    }
    private void listeners(){

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveImage();
                Toast.makeText(getActivity(), ""+user.getUid(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveImage(){

        Intent picture = new Intent();
        picture.setAction(Intent.ACTION_GET_CONTENT);
        picture.setType("image/*");
        startActivityForResult(picture, Gallery_Req);

        storageImage = FirebaseStorage.getInstance().getReference().child( "SnapShop gallery" );

        if (ImageUri != null){
            final StorageReference filepath = storageImage.child( ImageUri.getLastPathSegment() );
            filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            newRef = userGalaryRef.push();
                            newRef.setValue(downloadUrl);


                        }
                    });
                }
            });
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Req && resultCode == RESULT_OK) {
            Uri imageuri = data.getData();
            CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(getContext(),this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            ImageUri = result.getUri();
            if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "Error 2" + error, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
