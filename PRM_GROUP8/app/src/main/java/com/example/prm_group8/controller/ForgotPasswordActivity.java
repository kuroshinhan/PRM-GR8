package com.example.prm_group8.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_group8.database.DBHelper;
import com.example.prm_group8.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";
    private EditText etEmail;
    private Button sendOtpButton, btnBackToLogin;
    private FirebaseAuth mAuth;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        mAuth = FirebaseAuth.getInstance();
        dbHelper = new DBHelper(this);

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });


    }

    private void initializeViews() {
        etEmail = findViewById(R.id.emailEditText);
        sendOtpButton = findViewById(R.id.sendEmailButton);

    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Email đặt lại mật khẩu đã được gửi đến " + email,
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Email sent successfully");
                            backToLogin();
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Lỗi không xác định";
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                errorMessage = "Email chưa được đăng ký trong hệ thống";
                            }
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Gửi email thất bại: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Failed to send reset email", task.getException());
                        }
                    }
                });
    }

    private void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}