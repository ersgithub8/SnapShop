package com.fyp.snapshop.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fyp.snapshop.Adapters.EditorListAdapter;
import com.fyp.snapshop.Adapters.SliderAdapterExample;
import com.fyp.snapshop.JavaClass.RecyclerTouchListener;
import com.fyp.snapshop.Models.Model;
import com.fyp.snapshop.R;
import com.fyp.snapshop.activities.EditorProfilePreview;
import com.fyp.snapshop.activities.WebViewPlaylist;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {



    SliderView sliderView;
    RecyclerView recyclerView;
    EditorListAdapter adapter1;
    List<Model> data1;
    ImageButton ibPs,ibAi,ibAe,ibCd;
    String url1="https://www.youtube.com/watch?v=ZByhs9mDtDg&list=PLW-zSkCnZ-gA5Jn6gZtUa6-aG0OoRZyb6";
    String url2="https://www.youtube.com/watch?v=vd1vRpoWC3M&list=PLW-zSkCnZ-gCq0DjkzY-YapCBEk0lA6lR";
    String url3="https://www.youtube.com/watch?v=Xv8JBXPgeI8&list=PLW-zSkCnZ-gD8OcjTPu-u_Rxl9-kI9Xqr";
    String url4="https://www.youtube.com/watch?v=Pwclfazd4jQ&list=PLU4yvac0MJbLsl0sPjgaXXDgddviG6eYM";

    ArrayList<Double> ratingpoints=new ArrayList<>();
    //Firebase
    private FirebaseAuth mAuth;
    public FirebaseUser user;
    //Realtime Database
    FirebaseDatabase database;
    DatabaseReference myRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, null, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference().child("editor");

        imageSlider(view);
        bindViews(view);
        listners(view);

        data1 = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                data1.clear();



                for (DataSnapshot userSnapShot : dataSnapshot.getChildren()){
                    Model model = userSnapShot.getValue(Model.class);
                    String uid = userSnapShot.getKey();
                    model.setUid(uid);
                    data1.add(model);

                    if(userSnapShot.hasChild("rating")){
                        if(userSnapShot.child("rating").hasChild("AverageRating")){
                            if(userSnapShot.child("rating").child("AverageRating").hasChild("current")){
                                Double rating=Double.parseDouble(userSnapShot.child("rating").child("AverageRating").child("current").getValue().toString());

                                ratingpoints.add(rating);

                            }else{

                                ratingpoints.add(0.0);
                            }
                        }else{

                            ratingpoints.add(0.0);
                        }
                    }else{
                        ratingpoints.add(0.0);
                    }
                    adapter1 = new EditorListAdapter(data1,getActivity(),ratingpoints);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.hasFixedSize();
                    recyclerView.setAdapter(adapter1);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            adapter1.showShimmer=false;
                            adapter1.notifyDataSetChanged();

                        }
                    },2000);

                    adapter1.setOnClickListener(new EditorListAdapter.OnClickListner() {
                        @Override
                        public void onClick(int position, Model md) {
                            //Toast.makeText(getActivity(), ""+md.getUid(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), EditorProfilePreview.class).putExtra("UID",md.getUid()));
                        }
                    });


                  /*  recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Model md = data1.get(position);
                            startActivity(new Intent(getActivity(), EditorProfilePreview.class).putExtra("UID",md.getUid()));
                            //Toast.makeText(getActivity(), ""+md.getUid(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onLongClick(View view, int position) {
                            Model md = data1.get(position);
                            Toast.makeText(getActivity(), ""+md.getName(), Toast.LENGTH_SHORT).show();

                        }
                    }));*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }



    private void bindViews(View view) {


        recyclerView = view.findViewById(R.id.rv_editor_list);
        data1 = new ArrayList<>();
        ibPs = view.findViewById(R.id.ib_ps);
        ibAi = view.findViewById(R.id.ib_ai);
        ibAe = view.findViewById(R.id.ib_ae);
        ibCd = view.findViewById(R.id.ib_cd);


    }

    private void listners(View view) {
        ibPs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),WebViewPlaylist.class).putExtra("Key",url1));
            }
        });
        ibAe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),WebViewPlaylist.class).putExtra("Key",url2));
            }
        });
        ibAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),WebViewPlaylist.class).putExtra("Key",url3));
            }
        });
        ibCd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),WebViewPlaylist.class).putExtra("Key",url4));
            }
        });
    }

    private void imageSlider(View view) {
        sliderView = view.findViewById(R.id.imageSlider);
        SliderAdapterExample adapter = new SliderAdapterExample(getActivity());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.DROP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.POPTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

}