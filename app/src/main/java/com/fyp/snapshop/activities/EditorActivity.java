package com.fyp.snapshop.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fyp.snapshop.Adapters.ViewAdapter;
import com.fyp.snapshop.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class EditorActivity extends AppCompatActivity {

    TabLayout tl;
    ViewPager vp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        init();
        tabMenu();
    }

    private void init() {
        tl=findViewById(R.id.tabLayout);
        vp=findViewById(R.id.viewPager);

    }


    private void tabMenu() {


        tl.addTab(tl.newTab().setText("HOME"));
        tl.addTab(tl.newTab().setText("INBOX"));
        tl.addTab(tl.newTab().setText("PROFILE"));

        ViewAdapter va =new ViewAdapter(getSupportFragmentManager());
        vp.setAdapter(va);
        vp.setOffscreenPageLimit(3);
        vp.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener(tl));

        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }});
    }

}
