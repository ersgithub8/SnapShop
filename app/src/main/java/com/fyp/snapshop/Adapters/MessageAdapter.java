package com.fyp.snapshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.snapshop.Models.Message;
import com.fyp.snapshop.R;
import com.fyp.snapshop.activities.ImageViewerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageVH> {

    private List<Message> messageslist;
    Context context;

    FirebaseAuth auth;
    DatabaseReference userref;
    String currentuserid;
    public MessageAdapter(List<Message> messageslist, Context context) {
        this.messageslist = messageslist;
        this.context = context;
        auth= FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout,parent,false);


        return new MessageVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageVH holder, int position) {

        currentuserid=auth.getCurrentUser().getUid();
        Message message=messageslist.get(position);

        String fromuserid=message.getFrom();
//        String messagetype=message.getType();

        userref= FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserid);
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("image")){
                    String image=dataSnapshot.child("image").getValue().toString();

                    if(!image.equals(""))
                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.profileimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        holder.recievermessage.setVisibility(View.GONE);
        holder.profileimage.setVisibility(View.GONE);
//        holder.sendermessage.setVisibility(View.GONE);

        holder.recieveimage.setVisibility(View.GONE);
        holder.senderimage.setVisibility(View.GONE);

        //        holder.sendermessage.setVisibility(View.INVISIBLE);
//        if(messagetype.equals("text")){
//
//            if(fromuserid.equals(currentuserid)){
//                holder.sendermessage.setVisibility(View.VISIBLE);
//
//                holder.sendermessage.setBackgroundResource(R.drawable.sender_message_layout);
//                holder.sendermessage.setText(message.getMessage());
//            }else{
//
//                holder.sendermessage.setVisibility(View.INVISIBLE);
//
//
////                holder.sendermessage.setBackgroundResource(R.drawable.sender_message_layout);
//                holder.recievermessage.setText(message.getMessage());
//                holder.recievermessage.setVisibility(View.VISIBLE);
//                holder.profileimage.setVisibility(View.VISIBLE);
//
//            }
//        }else if(messagetype.equals("image")){
            holder.recieveimage.setVisibility(View.GONE);
            holder.senderimage.setVisibility(View.GONE);


            if(fromuserid.equals(currentuserid)){
                holder.senderimage.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(holder.senderimage);
            }else {

                holder.profileimage.setVisibility(View.VISIBLE);
                holder.recieveimage.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(holder.recieveimage);
            }
//        }

    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context.getApplicationContext(), ImageViewerActivity.class);
            intent.putExtra("url",message.getMessage());
            context.startActivity(intent);
        }
    });
    }

    @Override
    public int getItemCount() {
        return messageslist.size();
    }


    public class MessageVH extends RecyclerView.ViewHolder{

//        TextView sendermessage,recievermessage;
        CircleImageView profileimage;
        ImageView senderimage,recieveimage;
        public MessageVH(@NonNull View itemView) {
            super(itemView);
//
//            sendermessage=itemView.findViewById(R.id.sendermessagetext);
//            recievermessage=itemView.findViewById(R.id.recievermessagetext);
            profileimage=itemView.findViewById(R.id.messagepimage);

            senderimage=itemView.findViewById(R.id.messagesenderimage);
            recieveimage=itemView.findViewById(R.id.messagerecieverimage);

        }
    }
}

