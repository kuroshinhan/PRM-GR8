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

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;
import com.example.prm_group8.model.User;
import com.example.prm_group8.controller.ForgotPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.database.Cursor; // Thêm import này
import android.database.sqlite.SQLiteDatabase; // Đã thêm trước đó

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

    private void performLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String input = emailEditText.getText().toString().trim(); // Có thể là email hoặc tên người dùng
        String password = passwordEditText.getText().toString().trim();

        if (input.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập cả email/tên người dùng và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem input là email hay tên người dùng
        String email;
        if (input.equalsIgnoreCase("admin")) {
            email = "han171023fpt@gmail.com"; // Ánh xạ "admin" sang email
        } else if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            Toast.makeText(this, "Địa chỉ email hoặc tên người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else {
            email = input;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                User localUser = dbHelper.getUserByEmail(email);
                                if (localUser != null) {
                                    Log.d(TAG, "User role: " + localUser.getRole());
                                    if ("user".equalsIgnoreCase(localUser.getRole())) {
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công với tư cách là người dùng", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, HomeUser.class);
                                        intent.putExtra(EXTRA_USER_ID, localUser.getId());
                                        if (remember.isChecked()) {
                                            editor.putString("email", email);
                                            editor.putString("password", password);
                                            editor.apply();
                                        } else {
                                            editor.clear();
                                            editor.apply();
                                        }
                                        startActivity(intent);
                                    } else if ("admin".equalsIgnoreCase(localUser.getRole())) {
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công với tư cách Quản trị viên", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, HomeAdminActivity.class);
                                        intent.putExtra(EXTRA_USER_ID, localUser.getId());
                                        if (remember.isChecked()) {
                                            editor.putString("email", email);
                                            editor.putString("password", password);
                                            editor.apply();
                                        } else {
                                            editor.clear();
                                            editor.apply();
                                        }
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Vai trò người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Thông tin người dùng không tìm thấy trong cơ sở dữ liệu cục bộ", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Vui lòng xác minh email trước khi đăng nhập.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            editor.clear();
                            editor.apply();
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Login failed: " + task.getException().getMessage());
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