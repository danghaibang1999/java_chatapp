package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class ResetPasswordActivity extends AppCompatActivity {

    EditText newPasswordInput;
    EditText confirmPasswordInput;
    EditText otpInput;
    Button resetPasswordBtn;
    ProgressBar progressBar;
    public static final String RESET_PASSWORD_URL = "http://34.92.61.98/api/users/reset-password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        String email = getIntent().getStringExtra("email");

        newPasswordInput = findViewById(R.id.new_password);
        confirmPasswordInput = findViewById(R.id.confirm_new_password);
        otpInput = findViewById(R.id.otp_reset_password);
        resetPasswordBtn = findViewById(R.id.reset_password_btn);
        progressBar = findViewById(R.id.progress_bar);

        setInProgress(false);

        resetPasswordBtn.setOnClickListener(v -> {
            String newPassword = newPasswordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            String otpInput = this.otpInput.getText().toString();
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ResetPasswordActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ResetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (newPassword.length() < 6) {
                Toast.makeText(ResetPasswordActivity.this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(otpInput, email, newPassword);
            }
        });


    }

    private void resetPassword(String otp, String email, String newPassword) {
        JSONObject request = new JSONObject();
        try {
            request.put("otp", otp);
            request.put("password", newPassword);
            request.put("email", email);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //Creating a string request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, RESET_PASSWORD_URL, request, response -> {
                    //If we are getting success from server
                    try {
                        if (response.get("msg").equals("ok")) {
                            setInProgress(false);
                            Toast.makeText(ResetPasswordActivity.this, "Password is reset successfully!!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(ResetPasswordActivity.this, "Invalid OTP", Toast.LENGTH_LONG).show();
                            setInProgress(false);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
                    try {
                        Toast.makeText(ResetPasswordActivity.this, new String(error.networkResponse.data, "UTF-8"), Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    setInProgress(false);
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void setInProgress(boolean isProgress) {
        if (isProgress) {
            newPasswordInput.setEnabled(false);
            confirmPasswordInput.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            resetPasswordBtn.setVisibility(View.GONE);
        } else {
            newPasswordInput.setEnabled(true);
            confirmPasswordInput.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            resetPasswordBtn.setVisibility(View.VISIBLE);
        }
    }
}