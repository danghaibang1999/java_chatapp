package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.chatapp.R;
import com.example.chatapp.manager.ApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class SignUpActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Button signUpBtn;
    EditText usernameInput;
    EditText phoneInput;
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
        usernameInput = findViewById(R.id.signup_name);
        phoneInput = findViewById(R.id.signup_phone_number);
        emailInput = findViewById(R.id.signup_email_address);
        passwordInput = findViewById(R.id.signup_password);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        login = findViewById(R.id.login_text_view);
        loginWithPhone = findViewById(R.id.login_with_phone_number_text_view);

        setInProgress(false);
        signUpBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String avatarUrl = "https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes-thumbnail.png";
            signup(email, password, username, phone, avatarUrl);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginForgotPasswordActivity.class);
            intent.putExtra("isForgotPassword", true);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginAddressActivity.class);
            startActivity(intent);
            // Open sign up activity
        });

        loginWithPhone.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void signup(String email, String password, String username, String phone, String avatarUrl) {
        setInProgress(true);

        ApiManager apiManager = ApiManager.getInstance(this);
        apiManager.signUp(username, username, email, password, phone, avatarUrl, new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("msg").equals("ok")) {

                        setInProgress(false);
                        Toast.makeText(SignUpActivity.this, "OTP send Successful", Toast.LENGTH_SHORT).show();
                        //Starting Home activitfalse
                        Intent intent = new Intent(SignUpActivity.this, LoginOTPActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        startActivity(intent);
                    } else {
                        //If the server response is not success
                        //Displaying an error message on toast
                        Toast.makeText(SignUpActivity.this, "Invalid Information input", Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(SignUpActivity.this, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                setInProgress(false);
            }
        });
    }

    private void setInProgress(boolean isProgress) {
        if (isProgress) {
            usernameInput.setEnabled(false);
            phoneInput.setEnabled(false);
            emailInput.setEnabled(false);
            passwordInput.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.GONE);
        } else {
            usernameInput.setEnabled(true);
            phoneInput.setEnabled(true);
            emailInput.setEnabled(true);
            passwordInput.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            signUpBtn.setVisibility(View.VISIBLE);
        }
    }
}