package com.example.chatapp.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapp.MainActivity;
import com.example.chatapp.R;

import java.util.HashMap;
import java.util.Map;

public class LoginMainScreenActivity extends AppCompatActivity {

    public static final String SHARED_PREF_NAME = "shared_pref";
    public static final String ROLL_SHARED_PREF = "roll";
    public static final String LOGIN_URL = "https://34.92.61.98/api/auth/login/";

    ProgressBar progressBar;
    Button loginBtn;
    EditText emailInput;
    EditText passwordInput;
    TextView forgotPassword;
    TextView signUp;
    TextView loginWithPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main_screen);

        progressBar = findViewById(R.id.login_progress_bar);
        loginBtn = findViewById(R.id.login_btn);
        emailInput = findViewById(R.id.login_email_address);
        passwordInput = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        signUp = findViewById(R.id.sign_up_text_view);
        loginWithPhone = findViewById(R.id.login_with_phone_number_text_view);

        setInProgress(false);
        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            login(email, password);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
            intent.putExtra("isForgotPassword", true);
            startActivity(intent);
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
            // Open sign up activity
        });

        loginWithPhone.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
            startActivity(intent);
        });
    }

    private void login(String username, String password) {
        setInProgress(true);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    Log.d("Response", response);
                    //If we are getting success from server
                    if (response.equals("success")) {
                        //Creating a shared preference

                        SharedPreferences sp = LoginMainScreenActivity.this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

                        //Creating editor to store values to shared preferences
                        SharedPreferences.Editor editor = sp.edit();
                        //Adding values to editor
                        editor.putString(ROLL_SHARED_PREF, username);

                        //Saving values to editor
                        editor.apply();

                        setInProgress(false);
                        //Starting Home activity
                        Intent intent = new Intent(LoginMainScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginMainScreenActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    } else if (response.equals("failure")) {
                        //If the server response is not success
                        //Displaying an error message on toast
                        Toast.makeText(LoginMainScreenActivity.this, "Email or Password is not valid", Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    } else {
                        //If the server response is not success
                        //Displaying an error message on toast
                        Toast.makeText(LoginMainScreenActivity.this, "Invalid user cell or password", Toast.LENGTH_LONG).show();
                        setInProgress(false);
                    }
                },

                error -> {
                    Toast.makeText(LoginMainScreenActivity.this, "There is an error !!!", Toast.LENGTH_LONG).show();
                    setInProgress(false);
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("username_or_email", username);
                params.put("password", password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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