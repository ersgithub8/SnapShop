package com.fyp.snapshop.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.snapshop.Adapters.MessageAdapter;
import com.fyp.snapshop.Models.Message;
import com.fyp.snapshop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorChat extends AppCompatActivity {

    ImageView back,userimage;
    TextView username;
    FloatingActionButton addimg;
    String currentuserid,recieverid,imageref,name;


    List<Message> messages=new ArrayList<>();
    LinearLayoutManager linearLayoutManager;


    Uri fileuri;

    MessageAdapter adapter;
    FirebaseAuth auth;
    DatabaseReference rootref;
    RecyclerView chatlist;
    StorageTask uploadtask;
    String myurl;

    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_chat);

        back=findViewById(R.id.back);
        userimage=findViewById(R.id.userimg);
        username=findViewById(R.id.usernamechat);
        chatlist=findViewById(R.id.chatrv);

        addimg=findViewById(R.id.fab);
        if(!getIntent().getBooleanExtra("fab",true)) {
            addimg.hide();
        }

        auth=FirebaseAuth.getInstance();

        currentuserid=auth.getCurrentUser().getUid();
        recieverid=getIntent().getStringExtra("vid");


        imageref=getIntent().getStringExtra("image");
        name=getIntent().getStringExtra("uname");


        username.setText(name);
//            Toast.makeText(this, name+"", Toast.LENGTH_SHORT).show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //...................


            linearLayoutManager=new LinearLayoutManager(this);
            adapter=new MessageAdapter(messages,this);

            chatlist.setLayoutManager(linearLayoutManager);
            chatlist.setAdapter(adapter);

        rootref= FirebaseDatabase.getInstance().getReference();



        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent,"Select Image"),438);

            }
        });

        if(!imageref.equals("")  && imageref != null) {
            Picasso.get().load(imageref).into(userimage);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        messages.clear();
        rootref.child("Message").child(currentuserid).child(recieverid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {





                Message message=dataSnapshot.getValue(Message.class);

                Log.v("abc",message+"");
                messages.add(message);

                if(messages.size()>0) {
                    for (int i = 0; i < messages.size() - 1; i++) {

                        if (messages.get(messages.size()-1).getMessageId().equals(messages.get(i).getMessageId())) {
                            messages.remove(messages.size()-1);
                        } else {

                            adapter.notifyDataSetChanged();
                        }

                    }
                }else {
                    adapter.notifyDataSetChanged();
                }
                Log.i("Line",messages.size()+"");
                chatlist.smoothScrollToPosition(chatlist.getAdapter().getItemCount());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==438 && resultCode==RESULT_OK  && data!=null && data.getData()!=null){

            fileuri=data.getData();
            StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");


            final String messageSenderRef="Message/"+auth.getCurrentUser().getUid()+"/"+recieverid;

            final String messageRecieverRef="Message/"+recieverid+"/"+auth.getCurrentUser().getUid();

            DatabaseReference userMessageketeyref=rootref.child("Message").child(messageSenderRef).child(messageRecieverRef).push();

            final String messagepushid=userMessageketeyref.getKey();

            final StorageReference filepath=storageReference.child(messagepushid+".jpg");
            uploadtask=filepath.putFile(fileuri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful());
                    {
                        Uri downloadurl=task.getResult();
                        myurl=downloadurl.toString();




                        Map messageTextBody=new HashMap();
                        messageTextBody.put("message",myurl);
                        messageTextBody.put("name",fileuri.getLastPathSegment());
//                            messageTextBody.put("type",checker)
                        messageTextBody.put("from",auth.getCurrentUser().getUid());
                        messageTextBody.put("to",recieverid);
                        messageTextBody.put("messageId",messagepushid);

                        Map messagebodydetail=new HashMap();
                        messagebodydetail.put(messageSenderRef+"/"+messagepushid,messageTextBody);
                        messagebodydetail.put(messageRecieverRef +"/"+messagepushid,messageTextBody);


                        rootref.updateChildren(messagebodydetail).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if(task.isSuccessful()){
                                }else {

                                    Toast.makeText(EditorChat.this, task.getException()+"", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });


        }
    }


}