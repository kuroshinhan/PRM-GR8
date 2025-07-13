package com.example.prm_group8.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;
import com.example.prm_group8.model.User;

public class RegisterAccount extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword, etEmail;
    private Button btnRegister;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        initializeViews();
        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirmPassword);
        etEmail = findViewById(R.id.email);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email đã tồn tại. Vui lòng sử dụng email khác.", Toast.LENGTH_LONG).show();
            return;
        }

        if (validateInput(username, password, confirmPassword, email)) {
            String role = "user";

            boolean success = dbHelper.addUser(username, password, email, role, null);

            if (success) {
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                loginUser(email, password);
            } else {
                Toast.makeText(this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser(String email, String password) {
        User user = dbHelper.getUserByEmailAndPassword(email, password);
        if (user != null) {
            Intent intent = new Intent(this, HomeUser.class);
            intent.putExtra("USER_ID", user.getId());
            startActivity(intent);
            finish();
        } else {
            backToLogin();
        }
    }

    private boolean validateInput(String username, String password, String confirmPassword, String email) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Tất cả các trường đều bắt buộc", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải dài ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void backToLogin(View view) {
        backToLogin();
    }

    private void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}