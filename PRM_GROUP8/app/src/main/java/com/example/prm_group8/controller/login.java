package com.example.prm_group8.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm_group8.DBHelper;
import com.example.prm_group8.R;
import com.example.prm_group8.model.User;
import com.example.prm_group8.controller.ForgotPassword;

public class login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String EXTRA_USER_ID = "USER_ID";

    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private DBHelper dbHelper;
    private TextView forgotPassTextView;
    private CheckBox remember;
    SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        initializeViews();
        dbHelper = new DBHelper(this);

        String name = sharedPreferences.getString("phoneNumber", "");
        String Pass = sharedPreferences.getString("password", "");

        phoneNumberEditText.setText(name);
        passwordEditText.setText(Pass);
        if(name.length()!=0&&Pass.length()!=0){
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
                Intent intent = new Intent(login.this, RegisterAccount.class);
                startActivity(intent);
            }
        });
        forgotPassTextView.setOnClickListener(new View.OnClickListener() { // Add this listener
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);
        signUpTextView = findViewById(R.id.textView);
        forgotPassTextView = findViewById(R.id.forgotPass);
        remember = findViewById(R.id.remember);
    }

    private void performLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both phone number and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Log.d(TAG, "Login button clicked");
            Log.d(TAG, "Phone: " + phoneNumber + ", Password: " + password);

            User user = dbHelper.getUserByPhoneAndPassword(phoneNumber, password);
            Log.d(TAG, "User found: " + (user != null));

            if (user != null && user.getRole() != null) {
                Log.d(TAG, "User role: " + user.getRole());
                if ("user".equalsIgnoreCase(user.getRole())) {
                    Toast.makeText(this, "Login Successful as User", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, Home.class);
                    intent.putExtra(EXTRA_USER_ID, user.getId());
                    if (remember.isChecked()) {
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        editor.putString("phoneNumber", phoneNumber); // Ví dụ: lưu một chuỗi
                        editor.putString("password", password);       // Ví dụ: lưu một số nguyên
                        editor.apply();
                    } else {
                        editor.clear();
                        editor.apply();
                    }
                    startActivity(intent);
                } else if ("admin".equalsIgnoreCase(user.getRole())) {
                    Toast.makeText(this, "Login Successful as Admin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomeAdminActivity.class);
                    intent.putExtra(EXTRA_USER_ID, user.getId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (remember.isChecked()) {
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        editor.putString("phoneNumber", phoneNumber); // Ví dụ: lưu một chuỗi
                        editor.putString("password", password);       // Ví dụ: lưu một số nguyên
                        editor.apply();
                    } else {
                        editor.clear();
                        editor.apply();
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Invalid user role", Toast.LENGTH_SHORT).show();
                }
            } else {
                editor.clear();
                editor.apply();
                Toast.makeText(this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login", e);
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}