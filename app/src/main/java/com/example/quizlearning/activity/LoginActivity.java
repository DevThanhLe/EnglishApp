package com.example.quizlearning.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnLoginListener;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnRecoveryListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.auth.AuthService;
import com.example.quizlearning.auth.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    TextView tvForgotPassword;
    EditText etEmail, etPassword;
    String email, password, username;
    Button btnLogin, btnRegister;
    FirebaseAuth mAuth;
    AuthService authService;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        authService = new AuthService();
        progressDialog = new ProgressDialog(LoginActivity.this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        btnRegister = findViewById(R.id.buttonRegister);

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Forgot Password
                //Recovery Password using Email
                new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, com.airbnb.lottie.R.style.Theme_AppCompat_DayNight))
                        .setTitle("Forgot Password")
                        .setMessage("Enter your email address to reset your password")
                        .setView(R.layout.dialog_recovery_password)
                        .setPositiveButton("Send", (dialog, which) -> {
                            EditText etEmail = ((AlertDialog) dialog).findViewById(R.id.etEmail);
                            email = etEmail.getText().toString().trim();
                            if (!validate(email)) {
                                Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                            } else if (email.isEmpty()) {
                                Toast.makeText(LoginActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                            } else {
                                authService.recoveryPassword(email, new OnRecoveryListener() {
                                    @Override
                                    public void onRecoverySuccess() {
                                        Toast.makeText(LoginActivity.this, "Recovery Email Sent", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(LoginActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onRecoveryFailure(Exception e) {
                                        Log.e(TAG, "onRecoveryFailure: "+ e.getMessage());
                                        Toast.makeText(LoginActivity.this, "Recovery Email Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        //Finding User from Database and checking if password matches


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty() && validate(email)) {
                    progressDialog.show();
                    //Login Successful
                    authService.login(email, password, new OnLoginListener() {
                        @Override
                        public void onLoginSuccess(UserModel currentUser) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            SharedPreferences sharedPreferences;
                            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            intent.putExtra("email", currentUser.getUserEmail());
                            editor.putString("username", currentUser.getUsername());
                            editor.putString("userId", currentUser.getUserID());
                            editor.apply();
                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onLoginFailure(Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect Authentication!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onLoginFailure: "+ e.getMessage() );
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Register Activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public boolean validate(String emailStr) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr).matches();
    }
}