package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOTPBtn;
    ProgressBar progressBar;
    TextView forgotPassword;
    TextView signUp;
    TextView loginWithMail;

    boolean isForgotPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_phone_number);

        isForgotPassword = getIntent().getBooleanExtra("isForgotPassword", false);

        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.login_mobile_numbers);
        sendOTPBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        signUp = findViewById(R.id.sign_up_text_view);
        loginWithMail = findViewById(R.id.login_email_address_text_view);

        findViewById(R.id.login_phone_number_linear_layout).setVisibility(isForgotPassword ? View.GONE : View.VISIBLE);
        forgotPassword.setVisibility(isForgotPassword ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
            intent.putExtra("isForgotPassword", true);
            startActivity(intent);
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
            // Open sign up activity
        });

        loginWithMail.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginMainScreenActivity.class);
            startActivity(intent);
        });

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOTPBtn.setOnClickListener((v) -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number is not valid");
                return;
            }
            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOTPActivity.class);
            intent.putExtra("isForgotPassword", isForgotPassword);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}