package com.fyp.snapshop.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fyp.snapshop.Adapters.GalleryAdapter;
import com.fyp.snapshop.Models.GalleryModel;
import com.fyp.snapshop.R;
import com.fyp.snapshop.notifications.utils.NotificationService;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.skydoves.elasticviews.ElasticCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.media.CamcorderProfile.get;

public class EditorProfilePreview extends AppCompatActivity {

   private TextView tv_editor_name,tv_editor_address;
   private RatingBar ratingBar;
   private ImageView iv_editor_pic;
    FloatingActionButton fab_cart;
    Button btn_call, btn_sms;
    RecyclerView rv_gallery;
    GalleryAdapter galleryAdapter;
    List<GalleryModel> list;

    LinearLayout rating;
    Uri fileuri;
    String ruserid;
    int i;
    ElasticCardView cardaddress;
    SweetAlertDialog loading;
    String pricee="0";
    EditText card,cvc,date;
    ArrayList<Uri> uriArrayList=new ArrayList<>();
    String imgurl;
    Button confirm;
    TextView price;
    //Firebase
    private FirebaseAuth mAuth;
    public FirebaseUser user;
    //Realtime Database
    FirebaseDatabase database;
    DatabaseReference myRef, myRef2;


    StorageTask uploadtask;

    DatabaseReference rootref;
    String myurl;
    String address,lat,lang;
    NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_profile_preview);


        notificationService = NotificationService.getInstance(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        ruserid=getIntent().getExtras().getString("UID");
        myRef2=database.getReference().child("Message").child(user.getUid());
        myRef=database.getReference().child("editor").child(ruserid);

        rating=findViewById(R.id.ratingll);
        card=findViewById(R.id.cardno);
        date=findViewById(R.id.carddate);
        cvc=findViewById(R.id.cardcvc);
        tv_editor_address=findViewById(R.id.address);
        cardaddress=findViewById(R.id.card_address);
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(date.getText().toString().length()==2){
//
//                }
                if(date.getText().toString().length()==2 && start !=2)
                {
                    date.append("/");
                }
                if(date.getText().toString().length()==2 && start ==2 )
                {
                    date.getText().delete(date.getText().toString().length() - 1, date.getText().toString().length());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        rootref=FirebaseDatabase.getInstance().getReference();
        price=findViewById(R.id.price);
        confirm=findViewById(R.id.confirmpayment);

        loading=new SweetAlertDialog(EditorProfilePreview.this,SweetAlertDialog.PROGRESS_TYPE).
                setTitleText("Loading");
        loading.setCancelable(false);
        bindViews();
        listeners();
        galleryLoad();


        myRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_editor_name.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(ruserid)){
                    rating.setVisibility(View.VISIBLE);
                }else{
                    rating.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                address=dataSnapshot.child("address").getValue().toString();
                if(dataSnapshot.hasChild("latitude"))
                lat=dataSnapshot.child("latitude").getValue().toString();
                if(dataSnapshot.hasChild("longitude"))
                    lang=dataSnapshot.child("longitude").getValue().toString();
                tv_editor_address.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.child("price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pricee=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    imgurl=dataSnapshot.getValue().toString();
                    Glide.with(EditorProfilePreview.this).load(dataSnapshot.getValue().toString()).into(iv_editor_pic);

                }else{
                    imgurl="";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cardaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://maps.google.com/?q="
                        +lat+","+lang); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        SharedPreferences prefs = getSharedPreferences("firebase_token", Context.MODE_PRIVATE);
        String tok = prefs.getString("token","");


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(card.getText().toString().equals("4242424242424242")
                && date.getText().toString().equals("01/25")
                && cvc.getText().toString().equals("123"))
                {
                if(uriArrayList.size()>0){

//                    Toast.makeText(EditorProfilePreview.this, uriArrayList.size()+"", Toast.LENGTH_SHORT).show();
                    for(int i=0;i<uriArrayList.size();i++){
                        sendimage(uriArrayList.get(i));
                    }
                    uriArrayList.clear();
                    confirm.setText("Pay");
                }else{
                    TapTargetView.showFor(EditorProfilePreview.this,
                            TapTarget.forView(fab_cart,"No Image Selected","Select any Image from here.")
                                    .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                    .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                    .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                    .titleTextColor(R.color.white)      // Specify the color of the title text
                                    .descriptionTextSize(14)            // Specify the size (in sp) of the description text
                                    .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                    .textColor(R.color.white)            // Specify a color for both the title and description text
                                    .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                    .dimColor(R.color.white)            // If set, will dim behind the view with 30% opacity of the given color
                                    .drawShadow(true)                   // Whether to draw a drop shadow or not
                                    .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                    .tintTarget(true)                   // Whether to tint the target view's color
                                    .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
//                    .icon(Drawable)                     // Specify a custom drawable to draw as the target
                                    .targetRadius(60),
                            new TapTargetView.Listener(){

                                @Override
                                public void onTargetClick(TapTargetView view) {
                                    super.onTargetClick(view);
                                    Intent intent=new Intent();
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    startActivityForResult(intent.createChooser(intent,"Select Image"),438);

                                }
                            }

                    );
                }
            }else {
                    SweetAlertDialog alertDialog=new SweetAlertDialog(EditorProfilePreview.this,
                            SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Invalid Card Credentials")
                            .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setConfirmButtonBackgroundColor(Color.RED);
                       alertDialog.setCancelable(false);
                       alertDialog.show();
                }

            }
        });

        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationService.sendNotification(tok, " Hello", "Hello World");
                Toast.makeText(EditorProfilePreview.this, ""+tok, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                Log.e("ErrorToken", tok);
            }
        });


    }


    private void bindViews() {
        tv_editor_name = findViewById(R.id.tv_editor_name);
        iv_editor_pic= findViewById(R.id.iv_editor_img);
        fab_cart = findViewById(R.id.editor_fab);
        rv_gallery = findViewById(R.id.rv_editor_galary);
        btn_call = findViewById(R.id.btn_Call);
        btn_sms = findViewById(R.id.btn_msg);
        ratingBar = findViewById(R.id.ratingbar_editor);
    }

    private void listeners() {

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.child("number").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String number = dataSnapshot.getValue().toString();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+number));
                        startActivity(intent);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef.child("number").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String number = dataSnapshot.getValue().toString();
                        // The number on which you want to send SMS
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        /*ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                myRef2 = database.getReference().child("editor").child(getIntent().getExtras().getString("UID")).child("Rating");
                myRef2.child(user.getUid()).setValue(rating);
               // Toast.makeText(EditorProfilePreview.this, ""+rating, Toast.LENGTH_SHORT).show();

            }
        });*/


        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dbRef1 = FirebaseDatabase.getInstance().getReference();
         ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


                        dbRef.child("editor").child(getIntent().getExtras().getString("UID")).child("rating").child("clients").child(user.getUid()).child("rating").setValue(rating);
            }
        });


        dbRef1.child("editor").child(getIntent().getExtras().getString("UID")).child("rating").child("clients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double total = 0.0;
                double count = 0.0;
                double average = 0.0;

                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    double rating = Double.parseDouble(ds.child("rating").getValue().toString());
                    total = total + rating;
                    count = count + 1;
                    average = total / count;
                }

                final DatabaseReference newRef = dbRef.child("editor").child(getIntent().getExtras().getString("UID")).child("rating").child("AverageRating");
                newRef.child("current").setValue(average);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });







    }

    private void galleryLoad() {
        myRef.child("gallery").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list = new ArrayList<>();
                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()){

                    GalleryModel galleryModel = new GalleryModel();
                    galleryModel.setGalleryImage(userSnapShot.getValue().toString());
                    list.add(galleryModel);

                    galleryAdapter = new GalleryAdapter(list,EditorProfilePreview.this);
                    rv_gallery.setLayoutManager(new GridLayoutManager(EditorProfilePreview.this,3));
                    rv_gallery.setAdapter(galleryAdapter);

                    galleryAdapter.setOnClickListener(new GalleryAdapter.OnClickListner() {
                        @Override
                        public void onClick(int position, GalleryModel gm) {
                            startActivity(new Intent(EditorProfilePreview.this, ImagePreviewActivity.class).putExtra("CODE",0).putExtra("IMGURL", gm.getGalleryImage()));

                        }
                    });

                    galleryAdapter.notifyDataSetChanged();

                    rv_gallery.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Toast.makeText(EditorProfilePreview.this, "plz calm", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==438 && resultCode==RESULT_OK  ){


//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            ClipData clipData=data.getClipData();

            if(clipData  != null){
                for(int i=0;i<clipData.getItemCount();i++){
                    uriArrayList.add(clipData.getItemAt(i).getUri());
//                    sendimage(clipData.getItemAt(i).getUri());
//                    Toast.makeText(this, i+"", Toast.LENGTH_SHORT).show();
                }
            }else {
//                sendimage(data.getData());
                uriArrayList.add(data.getData());
//                fileuri = data.getData();
            }

//            Toast.makeText(this, uriArrayList.size()+"", Toast.LENGTH_SHORT).show();

            confirm.setText("Pay    "+Integer.parseInt(pricee)*uriArrayList.size());
//            price.setText(Integer.parseInt(pricee)*uriArrayList.size()+"");

//            sendimage(uriArrayList);
        }
    }

    public void sendimage(Uri fileuri){

//        int i;
//        for (i=0;i<uris.size();i++) {
            loading.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

            final String messageSenderRef = "Message/" + mAuth.getCurrentUser().getUid() + "/" + ruserid;
            final String messageRecieverRef = "Message/" + ruserid + "/" + mAuth.getCurrentUser().getUid();
            DatabaseReference userMessageketeyref = rootref.child("Message").child(messageSenderRef).child(messageRecieverRef).push();
            final String messagepushid = userMessageketeyref.getKey();
            final StorageReference filepath = storageReference.child(messagepushid + ".jpg");

            uploadtask = filepath.putFile(fileuri);


            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadurl = task.getResult();
                        myurl = downloadurl.toString();


                        Map messageTextBody = new HashMap();
                        messageTextBody.put("message", myurl);
                        messageTextBody.put("name", fileuri.getLastPathSegment());
//                            messageTextBody.put("type",checker)
                        messageTextBody.put("from", mAuth.getCurrentUser().getUid());
                        messageTextBody.put("to", ruserid);
                        messageTextBody.put("messageId", messagepushid);

                        Map messagebodydetail = new HashMap();
                        messagebodydetail.put(messageSenderRef + "/" + messagepushid, messageTextBody);
                        messagebodydetail.put(messageRecieverRef + "/" + messagepushid, messageTextBody);


                        rootref.updateChildren(messagebodydetail).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (task.isSuccessful()) {
                                    loading.dismiss();
                                    Intent intent=new Intent(EditorProfilePreview.this,EditorChat.class);
                                    intent.putExtra("fab",false);
                                    intent.putExtra("vid",ruserid);
                                    intent.putExtra("uname",tv_editor_name.getText().toString());
                                    intent.putExtra("image",imgurl );
                                    startActivity(intent);
                                    finish();
                                } else {
                                    loading.dismiss();
                                    Toast.makeText(EditorProfilePreview.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(EditorProfilePreview.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

//        }

    }
//    public void loc(){
//        String address;
//        Double lat=address.get
//    }

}
