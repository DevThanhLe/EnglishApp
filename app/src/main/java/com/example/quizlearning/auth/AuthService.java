package com.example.quizlearning.auth;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.quizlearning.Services.FirestoreService.DefaultImage;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnChangePassListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnLoginListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnRecoveryListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnRegisterListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.QueryUserListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.User.OnAddUserListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthService {
    private static final String TAG = "AuthService";
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    ModifierData modifierData;
    UserModel currentUser;
    DefaultImage defaultImage = new DefaultImage();

    public AuthService() {
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        modifierData = new ModifierData();
    }

    public boolean isLogin() {
        return mAuth.getCurrentUser() != null;
    }

    public void getCurrentUser(QueryUserListener listener) {
        mStore
                .collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentUser = documentSnapshot.toObject(UserModel.class);
                    listener.onQueryUserSuccess(currentUser);
                })
                .addOnFailureListener(e -> {
                    listener.onQueryUserFailure(e);
                });
    }

    public void register(String name, String email, String password, OnRegisterListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        UserModel userModel = new UserModel(mAuth.getCurrentUser().getUid(), name, email, defaultImage.getDefaultUserImage());
                        modifierData.addUser(userModel, new OnAddUserListener() {
                            @Override
                            public void onAddUserSuccess() {
                                listener.onRegisterSuccess();
                            }

                            @Override
                            public void onAddUserFailure(Exception e) {
                                listener.onRegisterFailure(e);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onRegisterFailure(e);
                });
    }

    public void login(String email, String password, OnLoginListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getCurrentUser(new QueryUserListener() {
                            @Override
                            public void onQueryUserSuccess(UserModel currentUser) {
                                listener.onLoginSuccess(currentUser);
                            }

                            @Override
                            public void onQueryUserFailure(Exception e) {
                                Log.e(TAG, "onQueryUserFailure: " + e.getMessage());
                                listener.onLoginFailure(e);
                            }
                        });
                    } else {
                        listener.onLoginFailure(task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onLoginFailure(e);
                });
    }

    public void recoveryPassword(String email, OnRecoveryListener listener) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onRecoverySuccess();
                    } else {
                        listener.onRecoveryFailure(task.getException());
                    }
                });
    }

    public void logout() {
        mAuth.signOut();
    }

    public void changePassword(String oldPassword, String newPassword, String email, OnChangePassListener listener) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, oldPassword);

        FirebaseUser user = mAuth.getCurrentUser();
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            listener.onChangePassSuccess();
                                        } else {
                                            listener.onChangePassFailure(task1.getException());
                                        }
                                    });
                        } else {
                            listener.onChangePassFailure(task.getException());
                            Log.e(TAG, "AuthService: reauth " + task.getException().getMessage());
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    listener.onChangePassFailure(e);
                    Log.e(TAG, "AuthService: reauth " + e.getMessage());
                });
    }
}
