package com.example.quizlearning.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.QueryUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Folder.OnRemoveTopicInFolderListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Topic.OnRemoveTopicListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.User.OnUpdateUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Word.OnPutWordImageListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.Services.FirestoreService.DefaultImage;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.activity.ChangePasswordActivity;
import com.example.quizlearning.activity.LoginActivity;
import com.example.quizlearning.activity.MainActivity;
import com.example.quizlearning.auth.AuthService;
import com.example.quizlearning.auth.UserModel;
import com.example.quizlearning.controller.UploadWord;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";

    TextView btnLogout, btnEditProfile, tvUsername, tvEmail, btnChangePassword;
    CircleImageView userImage;
    AuthService authService;
    Uri uri;
    ProgressDialog progressDialog;
    DefaultImage defaultImage;
    String imgURL, userEmail, userId, userName;
    UserModel currentUser;
    ModifierData modifierData;
    SharedPreferences userPreferences;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        authService = new AuthService();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvUsername = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvEmail);
        userImage = view.findViewById(R.id.imgAvatar);
        defaultImage = new DefaultImage();
        progressDialog = new ProgressDialog(view.getContext());
        modifierData = new ModifierData();
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        userPreferences = view.getContext().getSharedPreferences("user", view.getContext().MODE_PRIVATE);
        Log.e(TAG, "onCreateView: "+userPreferences.toString() );
        if (authService.isLogin() && currentUser == null) {
            if (userPreferences.getString("username", "").isEmpty()) {
                progressDialog.show();
                authService.getCurrentUser(new QueryUserListener() {
                    @Override
                    public void onQueryUserSuccess(UserModel user) {
                        SharedPreferences.Editor editor = userPreferences.edit();
                        editor.putString("username", user.getUsername());
                        editor.putString("email", user.getUserEmail());
                        editor.putString("userImageURL", user.getUserImageURL());
                        editor.apply();
                        userName =user.getUsername();
                        userEmail = user.getUserEmail();
                        tvUsername.setText(userName);
                        tvEmail.setText(userEmail);
                        Glide.with(view.getContext())
                                .load(user.getUserImageURL())
                                .centerCrop()
                                .into(userImage);
                        currentUser = user;
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onQueryUserFailure(Exception e) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onQueryUserFailure: " + e.getMessage());
                    }
                });
            } else {
                userName = userPreferences.getString("username", "");
                userEmail = userPreferences.getString("email", "");
                userId = userPreferences.getString("userId", "");
                tvUsername.setText(userName);
                tvEmail.setText(userEmail);
                Glide.with(view.getContext())
                        .load(userPreferences.getString("userImageURL", ""))
                        .centerCrop()
                        .into(userImage);
            }
        } else {
            //Go to Login Activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = new EditText(view.getContext());
                editText.setPadding(20, 10, 20, 10);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Change User Name")
                        .setMessage("Enter new user name:")
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newUsername = editText.getText().toString().trim();
                                if (newUsername.isEmpty()) {
                                    editText.setError("Please enter new user name");
                                    editText.requestFocus();
                                }else {
                                    progressDialog.show();
                                    currentUser.setUsername(newUsername);
                                    modifierData.updateUser(currentUser, new OnUpdateUserListener() {
                                        @Override
                                        public void onUpdateUserSuccess() {
                                            tvUsername.setText(currentUser.getUsername());
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onUpdateUserFailure(Exception e) {
                                            progressDialog.dismiss();
                                            Log.e(TAG, "onUpdateUserFailure: " + e.getMessage() );
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logout
                //Go to Login Activity
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Do you want to logout?")
                        .setPositiveButton("Yes", (d, w) -> {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
//                            view.getContext().getSharedPreferences("MyPrefs", view.getContext().MODE_PRIVATE).edit().clear().apply();
                            userPreferences.edit().clear().apply();
                            startActivity(intent);
                            getActivity().finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), ChangePasswordActivity.class).putExtra("userEmail", currentUser.getUserEmail()));
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        userPreferences.edit().clear().apply();
        super.onDestroy();
    }

}

