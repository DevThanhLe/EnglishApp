package com.example.quizlearning.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quizlearning.InterfaceAsyncTaskListener.Auth.OnChangePassListener;
import com.example.quizlearning.R;
import com.example.quizlearning.auth.AuthService;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText etOldPassword, etNewPassword, etConfirmNewPassword;
    Button btnChangePassword, btnCancel;
    AuthService authService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnCancel = findViewById(R.id.btnCancel);
        authService = new AuthService();

        if (getIntent().getStringExtra("userEmail") == null) {
            Toast.makeText(this, "Unknown Email Address!" , Toast.LENGTH_SHORT).show();
            finish();
        }

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnChangePassword.setOnClickListener(v -> {
            String oldPassword = etOldPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            String confirmNewPassword = etConfirmNewPassword.getText().toString();
            String email = getIntent().getStringExtra("userEmail");
            if (validateInput(oldPassword, newPassword, confirmNewPassword)) {
                authService.changePassword(oldPassword, newPassword,email, new OnChangePassListener() {
                    @Override
                    public void onChangePassSuccess() {
                        Toast.makeText(ChangePasswordActivity.this, "Change password success!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void onChangePassFailure(Exception e) {
                        Toast.makeText(ChangePasswordActivity.this, "Change password failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private boolean validateInput(String oldPassword, String newPassword, String confirmNewPassword) {
        if (oldPassword.isEmpty()) {
            etOldPassword.setError("Old password is required");
            etOldPassword.requestFocus();
            return false;
        } else if (oldPassword.length() < 6) {
            etOldPassword.setError("Old password must be at least 6 characters");
            etOldPassword.requestFocus();
            return false;
        } else {
            etOldPassword.setError(null);
        }
        if (newPassword.isEmpty()) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return false;
        } else if (newPassword.length() < 6) {
            etNewPassword.setError("New password must be at least 6 characters");
            etNewPassword.requestFocus();
            return false;
        } else {
            etNewPassword.setError(null);
        }
        if (confirmNewPassword.isEmpty()) {
            etConfirmNewPassword.setError("Confirm new password is required");
            etConfirmNewPassword.requestFocus();
            return false;
        } else if (confirmNewPassword.length() < 6) {
            etConfirmNewPassword.setError("Confirm new password must be at least 6 characters");
            etConfirmNewPassword.requestFocus();
            return false;
        } else {
            etConfirmNewPassword.setError(null);
        }
        if (!newPassword.equals(confirmNewPassword)) {
            etConfirmNewPassword.setError("Confirm new password must be same with new password");
            etConfirmNewPassword.requestFocus();
            return false;
        } else {
            etConfirmNewPassword.setError(null);
        }
        return true;
    }
}