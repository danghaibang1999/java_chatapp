package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.chatapp.R;
import com.example.chatapp.manager.ApiManager;

import java.nio.charset.StandardCharsets;

public class LoginForgotPasswordActivity extends AppCompatActivity {
    EditText emailInput;
    Button sendOTPBtn;
    ProgressBar progressBar;
    TextView signUp;
    TextView loginWithMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.enter_email_to_get_otp_edit_text);
        sendOTPBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        signUp = findViewById(R.id.sign_up_text_view);
        loginWithMail = findViewById(R.id.login_email_address_text_view);

        progressBar.setVisibility(View.GONE);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        loginWithMail.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginAddressActivity.class);
            startActivity(intent);
        });

        sendOTPBtn.setOnClickListener((v) -> {
            requestOTP();
        });
    }

    private void requestOTP() {
        String email = emailInput.getText().toString();
        // Send OTP to email
        ApiManager apiManager = ApiManager.getInstance(this);
        apiManager.requestOTPStringRequest(email, new ApiManager.ApiStringListener() {

            @Override
            public void onResponse(String response) {
                if (response.equals("OK")) {

                    Intent intent = new Intent(LoginForgotPasswordActivity.this,
                            ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);

                } else {
                    // If the server response is not success
                    // Displaying an error message on toast
                    Toast.makeText(LoginForgotPasswordActivity.this,
                            "Invalid Information input", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(LoginForgotPasswordActivity.this,
                        new String(error.networkResponse.data, StandardCharsets.UTF_8),
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}