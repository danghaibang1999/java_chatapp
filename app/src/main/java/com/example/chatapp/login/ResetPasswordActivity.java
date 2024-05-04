package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.manager.ApiManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText newPasswordInput;
    EditText confirmPasswordInput;
    EditText otpInput;
    Button resetPasswordBtn;
    ProgressBar progressBar;

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
            String otp = otpInput.getText().toString();
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ResetPasswordActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ResetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (newPassword.length() < 6) {
                Toast.makeText(ResetPasswordActivity.this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(otp, email, newPassword);
            }
        });
    }

    private void resetPassword(String otp, String email, String newPassword) {
        ApiManager.getInstance(this).resetPassword(otp, email, newPassword,
                new ApiManager.ApiListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.get("msg").equals("ok")) {
                                setInProgress(false);
                                Toast.makeText(ResetPasswordActivity.this, "Password is reset successfully!!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, "Invalid OTP", Toast.LENGTH_LONG).show();
                                setInProgress(false);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(ResetPasswordActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    }
                }
        );
    }

    private void setInProgress(boolean isProgress) {
        newPasswordInput.setEnabled(!isProgress);
        confirmPasswordInput.setEnabled(!isProgress);
        otpInput.setEnabled(!isProgress);
        resetPasswordBtn.setEnabled(!isProgress);
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
    }
}
