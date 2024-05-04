package com.example.chatapp.manager;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiManager {
    // Singleton instance
    private static ApiManager instance;
    private final String BASE_URL = "http://34.92.61.98/api/";
    private final RequestQueue requestQueue;

    // Singleton constructor
    private ApiManager(Context context) {
        // Initialize the request queue
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // Singleton getInstance method
    public static synchronized ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager(context);
        }
        return instance;
    }

    // Method to handle API requests
    public void makeRequest(int method, String endpoint, JSONObject requestBody, final ApiListener listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method,
                BASE_URL + endpoint,
                requestBody,
                response -> {
                    if (listener != null) {
                        listener.onResponse(response);
                    }
                },
                error -> {
                    if (listener != null) {
                        listener.onError(error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    // Method to handle OTP request
    public void requestOTP(String email, final ApiListener listener) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        makeRequest(Request.Method.POST, "otps/request", requestBody, listener);
    }

    // Method to handle login API
    public void login(String usernameOrEmail, String password, final ApiListener listener) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username_or_email", usernameOrEmail);
            requestBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        makeRequest(Request.Method.POST, "auth/login", requestBody, listener);
    }

    public void signUp(String name, String username, String email, String password, String phone, String avatarUrl, final ApiListener listener) {
        // Construct the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", name);
            requestBody.put("username", username);
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("phone", phone);
            requestBody.put("avatar_url", avatarUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Make the API request
        makeRequest(Request.Method.POST, "users", requestBody, listener);
    }

    public void activeUser(String email, String otp, final ApiListener listener) {
        // Construct the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Make the API request
        makeRequest(Request.Method.PUT, "users/active", requestBody, listener);
    }

    public void resetPassword(String email, String newPassword, String otp, final ApiListener listener) {
        // Construct the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("new_password", newPassword);
            requestBody.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Make the API request
        makeRequest(Request.Method.PUT, "users/reset-password", requestBody, listener);
    }

    public void getConversations(String accessToken, String conversationId, final ApiListener listener) {
        // Construct the request URL
        String url = BASE_URL + "conversations/" + conversationId;

        // Make the API request with the access token in the header
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (listener != null) {
                        listener.onResponse(response);
                    }
                },
                error -> {
                    if (listener != null) {
                        listener.onError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set the access token in the header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    public void getProfile(String accessToken, final ApiListener listener) {
        // Construct the request URL
        String url = BASE_URL + "user/profile";

        // Make the API request with the access token in the header
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (listener != null) {
                        listener.onResponse(response);
                    }
                },
                error -> {
                    if (listener != null) {
                        listener.onError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set the access token in the header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    public void getUser(String userId, String authToken, final ApiListener listener) {
        // Construct the request URL with user ID
        String endpoint = "users/" + userId;

        // Make the API request with the access token in the header
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BASE_URL + endpoint, null,
                response -> {
                    if (listener != null) {
                        listener.onResponse(response);
                    }
                },
                error -> {
                    if (listener != null) {
                        listener.onError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set the access token in the header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

    public void getUserList(String accessToken, int page, int pageSize, String sortField, String sortType, String searchKeyword, final ApiListener listener) {
        // Construct the request URL
        String url = BASE_URL + "users?page=" + page + "&page_size=" + pageSize + "&sort=" + sortField + "&sort_type=" + sortType + "&search=" + searchKeyword;

        // Make the API request with the access token in the header
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (listener != null) {
                        listener.onResponse(response);
                    }
                },
                error -> {
                    if (listener != null) {
                        listener.onError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set the access token in the header
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }


    // Define an interface for API callbacks
    public interface ApiListener {
        void onResponse(JSONObject response);

        void onError(VolleyError error);
    }
}
