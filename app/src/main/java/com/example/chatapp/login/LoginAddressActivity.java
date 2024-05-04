package com.example.chatapp.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.manager.ApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class LoginAddressActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Button loginBtn;
    EditText emailInput;
    EditText passwordInput;
    TextView forgotPassword;
    TextView signUp;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_address);

        progressBar = findViewById(R.id.login_progress_bar);
        loginBtn = findViewById(R.id.login_btn);
        emailInput = findViewById(R.id.login_email_address);
        passwordInput = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        signUp = findViewById(R.id.sign_up_text_view);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        setInProgress(false);
        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            login(email, password);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginForgotPasswordActivity.class);
            startActivity(intent);
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            // Open sign up activity
        });
    }

    private void login(String username, String password) {
        setInProgress(true);
        ApiManager apiManager = ApiManager.getInstance(this);
        apiManager.login(username, password, new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.get("access_token").toString().isEmpty()) {
                        setInProgress(false);
                        // After successfully obtaining the access token
                        String accessToken = response.get("access_token").toString(); // Replace with actual access token
                        saveAccessToken(accessToken);
                        //Starting Home activity
                        Intent intent = new Intent(LoginAddressActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginAddressActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        //If the server response is not success
                        //Displaying an error message on toast
                        Toast.makeText(LoginAddressActivity.this, "Invalid user cell or password", Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(LoginAddressActivity.this, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                setInProgress(false);
            }
        });
    }

    // Method to save access token locally
    public void saveAccessToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();
    }

    private void setInProgress(boolean isProgress) {
        if (isProgress) {
            emailInput.setEnabled(false);
            passwordInput.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            emailInput.setEnabled(true);
            passwordInput.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
}