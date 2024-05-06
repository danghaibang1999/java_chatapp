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
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private EditText phoneInput;
    private Button sendOTPBtn;
    private ProgressBar progressBar;
    private TextView forgotPassword;
    private TextView signUp;
    private TextView loginWithMail;

    private boolean isForgotPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);
        initializeViews();

        isForgotPassword = getIntent().getBooleanExtra("isForgotPassword", false);
        handleVisibility();

        forgotPassword.setOnClickListener(v -> navigateToForgotPassword());

        signUp.setOnClickListener(v -> navigateToSignUp());

        loginWithMail.setOnClickListener(v -> navigateToLoginWithEmail());

        sendOTPBtn.setOnClickListener(v -> sendOTP());
    }

    private void initializeViews() {
        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.login_mobile_numbers);
        sendOTPBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        signUp = findViewById(R.id.sign_up_text_view);
        loginWithMail = findViewById(R.id.login_email_address_text_view);
    }

    private void handleVisibility() {
        int visibility = isForgotPassword ? View.GONE : View.VISIBLE;
        findViewById(R.id.login_phone_number_linear_layout).setVisibility(visibility);
        forgotPassword.setVisibility(visibility);
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
        intent.putExtra("isForgotPassword", true);
        startActivity(intent);
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    private void navigateToLoginWithEmail() {
        Intent intent = new Intent(this, LoginMainScreenActivity.class);
        startActivity(intent);
    }

    private void sendOTP() {
        if (!countryCodePicker.isValidFullNumber()) {
            phoneInput.setError("Phone number is not valid");
            return;
        }
        Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOTPActivity.class);
        intent.putExtra("isForgotPassword", isForgotPassword);
        intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
        startActivity(intent);
    }
}
