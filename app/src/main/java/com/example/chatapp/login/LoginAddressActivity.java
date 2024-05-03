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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapp.MainActivity;
import com.example.chatapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginAddressActivity extends AppCompatActivity {
    public static final String LOGIN_URL = "http://34.92.61.98/api/auth/login";

    ProgressBar progressBar;
    Button loginBtn;
    EditText emailInput;
    EditText passwordInput;
    TextView forgotPassword;
    TextView signUp;


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
        JSONObject request = new JSONObject();
        try {
            request.put("username_or_email", username);
            request.put("password", password);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //Creating a string request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, LOGIN_URL, request, response -> {
                    //If we are getting success from server
                    try {
                        if (!response.get("access_token").toString().isEmpty()) {
                            setInProgress(false);
                            //Starting Home activity
                            Intent intent = new Intent(LoginAddressActivity.this, MainActivity.class);
                            intent.putExtra("access_token", response.get("access_token").toString());
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
                },

                error -> {
                    try {
                        Toast.makeText(LoginAddressActivity.this, new String(error.networkResponse.data, "UTF-8"), Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    setInProgress(false);
                });

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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