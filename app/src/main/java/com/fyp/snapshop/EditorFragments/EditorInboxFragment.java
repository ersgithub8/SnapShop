package com.fyp.snapshop.EditorFragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fyp.snapshop.Models.ContactsEditor;
import com.fyp.snapshop.R;
import com.fyp.snapshop.activities.EditorChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditorInboxFragment extends Fragment {

    RecyclerView chat;


    DatabaseReference chatsreference,userref;

    View view;
    FirebaseAuth auth;
    String currentuserid;


    public EditorInboxFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_editor_inbox, container, false);

        chat=view.findViewById(R.id.chatcontactseditor);

        chat.setLayoutManager(new LinearLayoutManager(getContext()));

        auth=FirebaseAuth.getInstance();
        currentuserid=auth.getCurrentUser().getUid();

        chatsreference= FirebaseDatabase.getInstance().getReference().child("Message").child(currentuserid);

        userref= FirebaseDatabase.getInstance().getReference().child("customer");

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ContactsEditor> options=new FirebaseRecyclerOptions.Builder<ContactsEditor>()
                .setQuery(chatsreference,ContactsEditor.class)
                .build();


        FirebaseRecyclerAdapter<ContactsEditor,ChatViewHolder> adapter=new FirebaseRecyclerAdapter<ContactsEditor,
                ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull ContactsEditor
                    model) {

                final String userid=getRef(position).getKey();
                final String[] image = new String[1];
                userref.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            image[0]="";
                            if(dataSnapshot.hasChild("image")){
                                image[0] =dataSnapshot.child("image").getValue().toString();

                                if(!image[0].equals(""))
                                Picasso.get().load(image[0]).into(holder.profileimage);
                            }


                            final String retname=dataSnapshot.child("name").getValue().toString();
                            final String rettype=dataSnapshot.child("type").getValue().toString();

                            holder.username.setText(retname);
                            holder.usertype.setText(rettype);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getContext(), EditorChat.class);
                                    intent.putExtra("fab",true);
                                    intent.putExtra("vid",userid);
                                    intent.putExtra("uname",retname);
                                    intent.putExtra("image", image[0]);

                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_display,parent,false);



                return new ChatViewHolder(view);
            }
        };

        chat.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileimage;
        TextView username,usertype;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimage=itemView.findViewById(R.id.userprofileimage);
            username=itemView.findViewById(R.id.username);
            usertype=itemView.findViewById(R.id.usertype);

        }
    }
}
