package com.example.quizlearning.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.quizlearning.fragment.FavoriteFragment;
import com.example.quizlearning.fragment.FolderFragment;
import com.example.quizlearning.fragment.TopicFragment;

public class MyPagerAdapter extends FragmentStateAdapter {

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về Fragment tương ứng với vị trí
        switch (position) {
            case 0:
                return new TopicFragment();
            case 1:
                return new FolderFragment();
            case 2:
                return new FavoriteFragment();
            default:
                return new Fragment(); // hoặc trả về Fragment mặc định
        }
    }
    @Override
    public int getItemCount() {
        // Số lượng trang (Fragment) trong ViewPager2
        return 3;
    }
}

