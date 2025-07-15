package com.example.prm_group8.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_group8.database.DBHelper;
import com.example.prm_group8.R;
import com.example.prm_group8.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String EXTRA_USER_ID = "USER_ID";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private DBHelper dbHelper;
    private TextView forgotPassTextView;
    private CheckBox remember;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        dbHelper = new DBHelper(this);

        initializeViews();

        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        emailEditText.setText(savedEmail);
        passwordEditText.setText(savedPassword);
        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            remember.setChecked(true);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterAccount.class);
                startActivity(intent);
            }
        });

        forgotPassTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        Log.d(TAG, "Initializing views");
        try {
            emailEditText = findViewById(R.id.email);
            passwordEditText = findViewById(R.id.password);
            loginButton = findViewById(R.id.btn_login);
            signUpTextView = findViewById(R.id.textView);
            forgotPassTextView = findViewById(R.id.forgotPass);
            remember = findViewById(R.id.remember);

            if (emailEditText == null || passwordEditText == null || loginButton == null ||
                    signUpTextView == null || forgotPassTextView == null || remember == null) {
                throw new IllegalStateException("Một hoặc nhiều view không được tìm thấy trong layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khởi tạo view: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void createUserInLocalDatabase(FirebaseUser firebaseUser, String email) {
        try {
            if (!dbHelper.isEmailExists(email)) {
                String username = firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "User";
                String role = email.equals("han171023fpt@gmail.com") ? "admin" : "user";

                boolean success = dbHelper.addUser(username, email, role, null);
                if (success) {
                    Log.d(TAG, "User created in local database for email: " + email);
                    dbHelper.syncEmailVerificationStatus(email, firebaseUser.isEmailVerified());
                } else {
                    Log.e(TAG, "Failed to add user to local database for email: " + email);
                    Toast.makeText(this, "Lỗi tạo user trong cơ sở dữ liệu cục bộ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "User already exists in local database for email: " + email);
                // Cập nhật trạng thái verified nếu cần
                dbHelper.syncEmailVerificationStatus(email, firebaseUser.isEmailVerified());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating user in local database: " + e.getMessage());
            Toast.makeText(this, "Lỗi tạo user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void performLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String input = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (input.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập cả email/tên người dùng và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        String email;
        if (input.equalsIgnoreCase("admin")) {
            email = "han171023fpt@gmail.com";
        } else if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            Toast.makeText(this, "Địa chỉ email hoặc tên người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else {
            email = input;
        }

        Log.d(TAG, "Attempting login with email: " + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "Firebase login successful, user: " + (user != null ? user.getEmail() : "null"));
                            if (user != null && user.isEmailVerified()) {
                                User localUser = dbHelper.getUserByEmail(email);
                                Log.d(TAG, "Local user found: " + (localUser != null ? localUser.getEmail() : "null"));

                                if (localUser == null) {
                                    Log.d(TAG, "Creating new user in local database for: " + email);
                                    createUserInLocalDatabase(user, email);
                                    localUser = dbHelper.getUserByEmail(email); // Lấy lại sau khi tạo
                                }

                                if (localUser != null) {
                                    dbHelper.syncEmailVerificationStatus(email, user.isEmailVerified());
                                    Log.d(TAG, "User role: " + localUser.getRole());
                                    Intent intent;
                                    String message;

                                    if ("admin".equalsIgnoreCase(localUser.getRole())) {
                                        intent = new Intent(LoginActivity.this, HomeAdminActivity.class);
                                        message = "Đăng nhập thành công với tư cách Quản trị viên";
                                    } else {
                                        intent = new Intent(LoginActivity.this, HomeUser.class);
                                        message = "Đăng nhập thành công với tư cách là người dùng";
                                    }

                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                    intent.putExtra(EXTRA_USER_ID, localUser.getId());

                                    if (remember.isChecked()) {
                                        editor.putString("email", email);
                                        editor.apply();
                                    } else {
                                        editor.clear();
                                        editor.apply();
                                    }

                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Lỗi tạo thông tin người dùng", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Local user is null after creation attempt for email: " + email);
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Vui lòng xác minh email trước khi đăng nhập.", Toast.LENGTH_LONG).show();
                                Log.w(TAG, "Email not verified for: " + email);
                            }
                        } else {
                            editor.clear();
                            editor.apply();
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ: " + errorMessage, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Login failed: " + errorMessage);
                        }
                    }
                });
    }

    // Phương thức để ánh xạ email sang id (sử dụng DBHelper)
    private int getUserIdFromEmail(String email) {
        User user = dbHelper.getUserByEmail(email);
        return (user != null) ? user.getId() : -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}