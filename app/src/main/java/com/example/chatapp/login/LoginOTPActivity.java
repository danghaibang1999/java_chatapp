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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapp.MainActivity;
import com.example.chatapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class LoginOTPActivity extends AppCompatActivity {

    String phoneNumber;
    long timeoutSeconds = 60L;
    EditText otpInput;
    Button verifyOTPBtn;
    ProgressBar progressBar;
    TextView resendOtpTextView;
    public static final String OTP_REQUEST_URL = "http://34.92.61.98/api/otps/request";
    public static final String ACTIVE_USER_URL = "http://34.92.61.98/api/users/active";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_otp);

        String email = getIntent().getStringExtra("email");

        otpInput = findViewById(R.id.login_otp_code);
        verifyOTPBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        setInProgress(false);

        verifyOTPBtn.setOnClickListener(v -> {
            setInProgress(true);
            String enterOtpInput = otpInput.getText().toString();
            activeUser(enterOtpInput);
        });

        resendOtpTextView.setOnClickListener(v -> {
            sendOtp(email);
        });
    }

    private void sendOtp(String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, OTP_REQUEST_URL, jsonObject, response -> {
                    //If we are getting success from server
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
                }, error -> {
                    try {
                        Toast.makeText(LoginOTPActivity.this, new String(error.networkResponse.data, "UTF-8"), Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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

    private void activeUser(String otp) {
        JSONObject request = new JSONObject();
        try {
            request.put("otp", otp);
            request.put("email", getIntent().getStringExtra("email"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, ACTIVE_USER_URL, request, response -> {
                    //If we are getting success from server
                    try {
                        if (response.get("msg").equals("ok")) {
                            setInProgress(false);
                            Toast.makeText(LoginOTPActivity.this, "OTP verified", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginOTPActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(LoginOTPActivity.this, "Invalid OTP", Toast.LENGTH_LONG).show();
                            setInProgress(false);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
                    try {
                        Toast.makeText(LoginOTPActivity.this, new String(error.networkResponse.data, "UTF-8"), Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    setInProgress(false);
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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