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
import com.example.chatapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginForgotPasswordActivity extends AppCompatActivity {
    EditText emailInput;
    Button sendOTPBtn;
    ProgressBar progressBar;
    TextView signUp;
    TextView loginWithMail;
    public static final String OTP_REQUEST_URL = "http://34.92.61.98/api/otps/request";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.enter_email_to_get_otp_edit_text);
        sendOTPBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        signUp = findViewById(R.id.sign_up_text_view);
        loginWithMail = findViewById(R.id.login_email_address_text_view);

        progressBar.setVisibility(View.GONE);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            // Open sign up activity
        });

        loginWithMail.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginAddressActivity.class);
            startActivity(intent);
        });

        sendOTPBtn.setOnClickListener((v) -> {
            requestOTP();
        });
    }

    private void requestOTP() {
        String email = emailInput.getText().toString();
        // Send OTP to email
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

                    Intent intent = new Intent(LoginForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", emailInput.getText().toString());
                    startActivity(intent);


                } else {
                    //If the server response is not success
                    //Displaying an error message on toast
                    Toast.makeText(LoginForgotPasswordActivity.this, "Invalid Information input", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            try {
                Toast.makeText(LoginForgotPasswordActivity.this, new String(error.networkResponse.data, "UTF-8"), Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}