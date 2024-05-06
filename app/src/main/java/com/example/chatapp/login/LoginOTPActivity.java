package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.util.AndroidUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginOTPActivity extends AppCompatActivity {

    private static final long TIMEOUT_SECONDS = 60;
    private String phoneNumber;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private EditText otpInput;
    private Button nextBtn;
    private ProgressBar progressBar;
    private TextView resendOtpTextView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private boolean isForgotPassword = false;
    private CountDownTimer resendTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        initializeViews();
        isForgotPassword = getIntent().getBooleanExtra("isForgotPassword", false);
        phoneNumber = getIntent().getStringExtra("phone");

        findViewById(R.id.login_otp_linear_layout).setVisibility(isForgotPassword ? View.GONE : View.VISIBLE);

        sendOtp();

        nextBtn.setOnClickListener(v -> {
            verifyOtp();
        });

        resendOtpTextView.setOnClickListener(v -> {
            sendOtp();
        });
    }

    private void initializeViews() {
        otpInput = findViewById(R.id.login_otp_code);
        nextBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);
    }

    private void sendOtp() {
        setInProgress(true);
        startResendTimer();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                TIMEOUT_SECONDS,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signIn(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        AndroidUtil.showToast(LoginOTPActivity.this, "OTP verification failed");
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(s, token);
                        verificationCode = s;
                        resendingToken = token;
                        AndroidUtil.showToast(LoginOTPActivity.this, "OTP sent successfully");
                        setInProgress(false);
                    }
                });
    }

    private void verifyOtp() {
        String enterOtpInput = otpInput.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enterOtpInput);
        signIn(credential);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                navigateToNextScreen();
            } else {
                AndroidUtil.showToast(LoginOTPActivity.this, "OTP verification failed");
                setInProgress(false);
            }
        });
    }

    private void navigateToNextScreen() {
        Intent intent;
        if (isForgotPassword) {
            intent = new Intent(LoginOTPActivity.this, ResetPasswordActivity.class);
        } else {
            intent = new Intent(LoginOTPActivity.this, LoginUserNameActivity.class);
        }
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
        finish();
    }

    private void startResendTimer() {
        resendTimer = new CountDownTimer(TIMEOUT_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendOtpTextView.setText("Resend OTP in " + millisUntilFinished / 1000 + " seconds");
                resendOtpTextView.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resendOtpTextView.setText("Resend OTP");
                resendOtpTextView.setEnabled(true);
            }
        }.start();
    }

    private void setInProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        nextBtn.setVisibility(isProgress ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}
