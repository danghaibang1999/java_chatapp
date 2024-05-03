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
import com.example.chatapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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
    public static final String CREATE_USER_URL = "http://34.92.61.98/api/users";

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
            signup(email, password, username, phone);
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

    private void signup(String email, String password, String username, String phone) {
        setInProgress(true);

        JSONObject request = new JSONObject();
        try {
            request.put("username", username);
            request.put("phone", phone);
            request.put("email", email);
            request.put("password", password);
            request.put("avatar_url", "https://w7.pngwing.com/pngs/340/946/png-transparent-avatar-user-computer-icons-software-developer-avatar-child-face-heroes-thumbnail.png");
            request.put("name", username);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, CREATE_USER_URL, request, response -> {
                    //If we are getting success from server
                    try {
                        if (response.get("msg").equals("ok")) {

                            setInProgress(false);
                            Toast.makeText(SignUpActivity.this, "OTP send Successful", Toast.LENGTH_SHORT).show();
                            //Starting Home activitfalse
                            Intent intent = new Intent(SignUpActivity.this, LoginOTPActivity.class);
                            intent.putExtra("isForgotPassword", false);
                            intent.putExtra("email", email);
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
                }, error -> {
                    try {
                        Toast.makeText(SignUpActivity.this, new String(error.networkResponse.data, "UTF-8"), Toast.LENGTH_LONG).show();
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