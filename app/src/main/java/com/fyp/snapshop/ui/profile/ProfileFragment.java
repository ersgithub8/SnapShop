package com.fyp.snapshop.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fyp.snapshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    ImageView ivUserProfile;
    EditText etUserName, etUserPhone, etUserEmail, etUserAddress, etUserDOB, etUserPassword;
    Button logout;


    //Firebase
    private FirebaseAuth mAuth;
    public FirebaseUser user;
    //Realtime Database
    FirebaseDatabase database;
    DatabaseReference myRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("All user").child(user.getUid());

      View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile,null,false);
      bindviews(view);
      setviews();
      listener();


      return view;
    }



    private void bindviews(View view) {
        ivUserProfile = view.findViewById(R.id.iv_user_profile);
        etUserName = view.findViewById(R.id.et_user_name);
        etUserPhone = view.findViewById(R.id.et_user_number);
        etUserEmail = view.findViewById(R.id.et_user_email);
        etUserAddress = view.findViewById(R.id.et_address_number);
        etUserDOB = view.findViewById(R.id.et_user_dob);
        etUserPassword = view.findViewById(R.id.et_user_passwords);
        logout = view.findViewById(R.id.logout);
    }

    private void setviews(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                etUserName.setText(dataSnapshot.child("name").getValue(String.class));
                etUserPhone.setText(dataSnapshot.child("number").getValue(String.class));
                etUserEmail.setText(dataSnapshot.child("email").getValue(String.class));
                etUserAddress.setText(dataSnapshot.child("address").getValue(String.class));
                etUserDOB.setText(dataSnapshot.child("age").getValue(String.class));
                etUserPassword.setText(dataSnapshot.child("password").getValue(String.class));

                Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).apply(RequestOptions.circleCropTransform()).into(ivUserProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listener(){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                prefs.edit().remove("EMAIL").commit();
            }
        });
    }
}