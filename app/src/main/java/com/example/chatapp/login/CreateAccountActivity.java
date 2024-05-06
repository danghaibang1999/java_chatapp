package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

public class CreateAccountActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button signUpBtn;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView forgotPassword;
    private TextView login;
    private TextView loginWithPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initializeViews();

        signUpBtn.setOnClickListener(v -> {
            signUp();
        });

        forgotPassword.setOnClickListener(v -> {
            navigateToForgotPassword();
        });

        login.setOnClickListener(v -> {
            navigateToLogin();
        });

        loginWithPhone.setOnClickListener(v -> {
            navigateToPhoneLogin();
        });
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.login_progress_bar);
        signUpBtn = findViewById(R.id.signup_btn);
        emailInput = findViewById(R.id.signup_email_address);
        passwordInput = findViewById(R.id.signup_password);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        login = findViewById(R.id.login_text_view);
        loginWithPhone = findViewById(R.id.login_with_phone_number_text_view);
    }

    private void signUp() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            setInProgress(true);
            // Perform sign up
            // On success, call setInProgress(false);
            // On failure, call setInProgress(false);
        }
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
        intent.putExtra("isForgotPassword", true);
        startActivity(intent);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginMainScreenActivity.class);
        startActivity(intent);
        // Open sign up activity
    }

    private void navigateToPhoneLogin() {
        Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
        startActivity(intent);
    }

    private void setInProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        signUpBtn.setVisibility(isProgress ? View.GONE : View.VISIBLE);
    }
}
