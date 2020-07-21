package com.fyp.snapshop.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fyp.snapshop.EditorFragments.EditorHomeFragment;
import com.fyp.snapshop.EditorFragments.EditorInboxFragment;
import com.fyp.snapshop.EditorFragments.EditorProfileFragment;

public class ViewAdapter extends FragmentPagerAdapter {
    public ViewAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:return new EditorHomeFragment();
            case 1: return new EditorInboxFragment();
            case 2:return new EditorProfileFragment();
            default: return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
