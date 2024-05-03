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

public class SignUpActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button signUpBtn;
    EditText emailInput;
    EditText passwordInput;
    TextView forgotPassword;
    TextView login;
    TextView loginWithPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = findViewById(R.id.login_progress_bar);
        signUpBtn = findViewById(R.id.signup_btn);
        emailInput = findViewById(R.id.signup_email_address);
        passwordInput = findViewById(R.id.signup_password);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        login = findViewById(R.id.login_text_view);
        loginWithPhone = findViewById(R.id.login_with_phone_number_text_view);

        setInProgress(false);
        signUpBtn.setOnClickListener(v -> {
            emailInput.setEnabled(false);
            passwordInput.setEnabled(false);
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            signup(email, password);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
            intent.putExtra("isForgotPassword", true);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginAddressActivity.class);
            startActivity(intent);
            // Open sign up activity
        });

        loginWithPhone.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
            startActivity(intent);
        });
    }

    private void signup(String email, String password) {
        setInProgress(true);
        // Perform sign up
        // On success, call setInProgress(false);
        // On failure, call setInProgress(false);
    }

    private void setInProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            signUpBtn.setVisibility(View.VISIBLE);
        }
    }
}