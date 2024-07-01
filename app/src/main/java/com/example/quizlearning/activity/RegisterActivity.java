package com.example.quizlearning.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizlearning.Services.FirestoreService.DefaultImage;
import com.example.quizlearning.Services.FirestoreService.ModifierData;
import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnRegisterListener;
import com.example.quizlearning.ProgressDialog;
import com.example.quizlearning.R;
import com.example.quizlearning.auth.AuthService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    EditText etName, etEmail, etPassword, etConfirmPassword;
    String name, email, password, confirmPassword;
    TextView tvLogin;
    Button buttonRegister;
    FirebaseAuth mAuth;
    ModifierData modifierData;
    DefaultImage defaultImage = new DefaultImage();
    ProgressDialog progressDialog;
    AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        modifierData = new ModifierData(this);
        progressDialog = new ProgressDialog(this);

        etName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvLogin = findViewById(R.id.tvLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login Activity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();
                if (validateInput(name, email, password, confirmPassword)) {
                    progressDialog.show();
                    //Register User
                    authService.register(name, email, password, new OnRegisterListener() {
                        @Override
                        public void onRegisterSuccess() {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onRegisterFailure(Exception e) {
                            progressDialog.dismiss();
                            Log.e(TAG, "onAddUserFailure: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return false;
        } else {
            etName.setError(null);
        }
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        } else {
            etEmail.setError(null);
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return false;
        } else {
            etPassword.setError(null);
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirm Password is required");
            return false;
        } else {
            etConfirmPassword.setError(null);

        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password and Confirm Password must be same");
            return false;
        } else {
            etConfirmPassword.setError(null);
        }
        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            etEmail.setError("Invalid Email");
            return false;
        } else {
            etEmail.setError(null);
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;
        } else {
            etPassword.setError(null);
        }
        return true;
    }

    private void registerUser() {
        progressDialog.show();
    }
}