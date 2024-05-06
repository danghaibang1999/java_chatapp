package com.example.chatapp.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button resetPasswordBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initializeViews();
        setInProgress(false);
        setupResetPasswordButton();
    }

    private void initializeViews() {
        newPasswordInput = findViewById(R.id.new_password);
        confirmPasswordInput = findViewById(R.id.confirm_new_password);
        resetPasswordBtn = findViewById(R.id.reset_password_btn);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupResetPasswordButton() {
        resetPasswordBtn.setOnClickListener(v -> {
            newPasswordInput.setEnabled(false);
            confirmPasswordInput.setEnabled(false);
            String newPassword = newPasswordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            resetPassword(newPassword, confirmPassword);
        });
    }

    private void resetPassword(String newPassword, String confirmPassword) {
        if (newPassword.equals(confirmPassword)) {
            // Passwords match, proceed with password reset logic
            setInProgress(true);
        } else {
            // Passwords do not match, display error message
            newPasswordInput.setEnabled(true);
            confirmPasswordInput.setEnabled(true);
            confirmPasswordInput.setError("Passwords do not match");
            setInProgress(false);
        }
    }

    private void setInProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        resetPasswordBtn.setVisibility(isProgress ? View.GONE : View.VISIBLE);
    }
}
