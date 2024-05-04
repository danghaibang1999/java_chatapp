package com.example.chatapp.login;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.manager.ApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class LoginOTPActivity extends AppCompatActivity {
    long timeoutSeconds = 60L;
    EditText otpInput;
    Button verifyOTPBtn;
    ProgressBar progressBar;
    TextView resendOtpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_otp);

        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        otpInput = findViewById(R.id.login_otp_code);
        verifyOTPBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        setInProgress(false);

        verifyOTPBtn.setOnClickListener(v -> {
            setInProgress(true);
            String enterOtpInput = otpInput.getText().toString();
            activeUser(enterOtpInput, email, password);
        });

        resendOtpTextView.setOnClickListener(v -> {
            sendOtp(email);
        });
    }

    private void sendOtp(String email) {
        ApiManager apiManager = ApiManager.getInstance(this);
        apiManager.requestOTP(email, new ApiManager.ApiListener() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("msg").equals("ok")) {
                        Toast.makeText(LoginOTPActivity.this, "OTP send successfully", Toast.LENGTH_LONG).show();
                        timeoutSeconds = 60L;
                        startResendTimer();
                    } else {
                        //If the server response is not success
                        //Displaying an error message on toast
                        Toast.makeText(LoginOTPActivity.this, "Invalid Information input", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(LoginOTPActivity.this, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setInProgress(boolean isProgress) {
        if (isProgress) {
            otpInput.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            verifyOTPBtn.setVisibility(View.GONE);
        } else {
            otpInput.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            verifyOTPBtn.setVisibility(View.VISIBLE);
        }
    }

    private void activeUser(String otp, String email, String password) {
        ApiManager apiManager = ApiManager.getInstance(this);
        apiManager.activeUser(getIntent().getStringExtra("email"), otp, new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.get("msg").equals("ok")) {
                        setInProgress(false);
                        Toast.makeText(LoginOTPActivity.this, "OTP verified", Toast.LENGTH_SHORT).show();
                        login(email, password);
                    } else {
                        //If the server response is not success
                        //Displaying an error message on toast
                        Toast.makeText(LoginOTPActivity.this, "Invalid OTP", Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(LoginOTPActivity.this, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                setInProgress(false);
            }
        });
    }

    private void login(String email, String password) {
        ApiManager apiManager = ApiManager.getInstance(this);
        apiManager.login(email, password, new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.get("access_token").toString().isEmpty()) {
                        setInProgress(false);
                        // After successfully obtaining the access token
                        String accessToken = response.get("access_token").toString(); // Replace with actual access token
                        saveAccessToken(accessToken);
                        //Starting Home activity
                        Intent intent = new Intent(LoginOTPActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(LoginOTPActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        //If the server response is not success
                        //Displaying an error message on toast
                        Toast.makeText(LoginOTPActivity.this, "Invalid user cell or password", Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(LoginOTPActivity.this, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                setInProgress(false);
            }
        });
    }

    // Method to save access token locally
    public void saveAccessToken(String accessToken) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();
    }

    void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (timeoutSeconds > 0) {
                        timeoutSeconds--;
                        resendOtpTextView.setText("Resend OTP in " + timeoutSeconds + " seconds");
                    } else {
                        resendOtpTextView.setText("Resend OTP");
                        resendOtpTextView.setEnabled(true);
                        timer.cancel();
                    }
                });
            }
        }, 0, 1000);
    }
}