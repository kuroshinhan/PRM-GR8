package com.example.prm_group8.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;
import com.example.prm_group8.model.User;

public class RegisterAccount extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword, etPhoneNumber;
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
        etPhoneNumber = findViewById(R.id.phone_number);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (dbHelper.isPhoneNumberExists(phoneNumber)) {
            Toast.makeText(this, "Phone number already exists. Please use a different number.", Toast.LENGTH_LONG).show();
            return;
        }
        if (validateInput(username, password, confirmPassword, phoneNumber)) {
            String role = "user";

            boolean success = dbHelper.addUser(username, password, phoneNumber, role,null);

            if (success) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                loginUser(phoneNumber, password);
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser(String phoneNumber, String password) {
        User user = dbHelper.getUserByPhoneAndPassword(phoneNumber, password);
        if (user != null) {
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("USER_ID", user.getId());
            startActivity(intent);
            finish();
        } else {
            backToLogin();
        }
    }

    private boolean validateInput(String username, String password, String confirmPassword, String phoneNumber) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phoneNumber.matches("^0[0-9]{9}$")) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void backToLogin(View view) {
        backToLogin();
    }

    private void backToLogin() {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        finish();
    }
}