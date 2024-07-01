package com.example.quizlearning.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.quizlearning.R;
import com.example.quizlearning.adapter.MyPagerAdapter;
import com.example.quizlearning.model.FolderModel;
import com.example.quizlearning.model.TopicModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SearchView searchTopic, searchFolder;
    private List<FolderModel> dataFolder;
    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        View viewTopic = inflater.inflate(R.layout.fragment_topic, container, false);
        View viewFolder = inflater.inflate(R.layout.fragment_folder, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        searchTopic = viewTopic.findViewById(R.id.search);
        searchFolder = viewFolder.findViewById(R.id.search);
        // Khởi tạo Adapter và gắn nó với ViewPager2
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getActivity());
        viewPager.setAdapter(pagerAdapter);

        // Sử dụng TabLayoutMediator để kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Topics");
                    break;
                case 1:
                    tab.setText("Folders");

                    break;
                case 2:
                    tab.setText("Favorites");
                    break;
            }
        }).attach();

        return view;
    }

}
