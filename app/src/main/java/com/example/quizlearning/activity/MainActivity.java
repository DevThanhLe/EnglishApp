package com.example.quizlearning.activity;

import static com.example.quizlearning.R.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.quizlearning.R;
import com.example.quizlearning.fragment.CommunityFragment;
import com.example.quizlearning.fragment.HomeFragment;
import com.example.quizlearning.fragment.SettingFragment;
import com.example.quizlearning.fragment.TranslateFragment;
import com.example.quizlearning.fragment.QuizzFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity  extends AppCompatActivity{
    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences, userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);


        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        //default key is Username, default value is true (first login)
        String username = getIntent().getStringExtra("email");
        if (sharedPreferences.getBoolean(username, true)) {
            //First Login

            View view = getLayoutInflater().inflate(layout.slider_welcome_screen, null);
            ImageSlider imageSlider = view.findViewById(id.welcome_image_slider);
            ArrayList<SlideModel> slideModels = new ArrayList<>();
            slideModels.add(new SlideModel(drawable.home_tutorial, ScaleTypes.FIT));
            slideModels.add(new SlideModel(drawable.translate_tutorial, ScaleTypes.FIT));
            slideModels.add(new SlideModel(drawable.quizz_tutorial, ScaleTypes.FIT));
            slideModels.add(new SlideModel(drawable.community_tutorial, ScaleTypes.FIT));
            slideModels.add(new SlideModel(drawable.setting_tutorial, ScaleTypes.FIT));
            imageSlider.setImageList(slideModels, ScaleTypes.FIT);

            new AlertDialog.Builder(this)
                    .setTitle("Welcome")
                    .setMessage("Welcome to Quiz Learning App")
                    .setView(view)
                    .setPositiveButton("OK", null)
                    .setCancelable(false)
                    .show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(username, false);
            editor.apply();
        } else {
            //Not First Login
        }
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }
        bottomNavigationView = findViewById(id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == id.home_nav) {
                replaceFragment(new HomeFragment());
                return true;

            } else if (item.getItemId() == id.quizz_nav) {
                replaceFragment(new QuizzFragment());
                return true;

            } else if (item.getItemId() == id.profile_nav) {
                replaceFragment(new SettingFragment());
                return true;

            } else if (item.getItemId() == id.community_nav) {
                replaceFragment(new CommunityFragment());
                return true;
            } else {
                replaceFragment(new TranslateFragment());
                return true;
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Lắng nghe sự kiện khi bàn phím xuất hiện
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            private int rootViewHeight;

            @Override
            public boolean onPreDraw() {
                int newRootViewHeight = rootView.getHeight();

                // Kiểm tra nếu chiều cao của rootView thay đổi, có thể xem đó là một giả định về việc bàn phím được mở hay đóng
                if (newRootViewHeight != rootViewHeight) {
                    rootViewHeight = newRootViewHeight;

                    // Kiểm tra trạng thái của bàn phím
                    if (isKeyboardOpen(rootViewHeight)) {
                        // Bàn phím mở
                        bottomNavigationView.setVisibility(View.GONE); // Ẩn bottom menu
                    } else {
                        // Bàn phím đóng
                        bottomNavigationView.setVisibility(View.VISIBLE); // Hiện bottom menu
                    }
                }

                return true;
            }
        });
    }

    private boolean isKeyboardOpen(int rootViewHeight) {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int keypadHeight = screenHeight - rootViewHeight;

        return keypadHeight > screenHeight * 0.15; // Chỉ xem xét khi chiều cao bàn phím chiếm hơn 15% màn hình

    }

    @Override
    protected void onDestroy() {
        userPreferences.edit().clear().apply();
        super.onDestroy();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}