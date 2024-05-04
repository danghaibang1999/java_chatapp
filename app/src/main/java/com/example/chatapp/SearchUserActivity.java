package com.example.chatapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.chatapp.adapter.SearchUserRecyclerAdapter;
import com.example.chatapp.manager.ApiManager;

import org.json.JSONObject;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter searchUserRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.search_back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        backButton.setOnClickListener((v -> {
            onBackPressed();
        }));

        searchButton.setOnClickListener((v -> {
            String searchTerm = searchInput.getText().toString().trim();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid Username");
                return;
            }
            getUserList(searchTerm);
        }));
    }

    void getUserList(String searchTerm) {
        // Get the access token from your session management or wherever you store it
        String accessToken = "your_access_token";

        // Call the getUserList method from ApiManager
        ApiManager.getInstance(this).getUserList(accessToken, 1, 20, "name", "asc", searchTerm, new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                // Handle the response here
                // Pass the response JSONObject to the adapter for display

                searchUserRecyclerAdapter = new SearchUserRecyclerAdapter(response, getApplicationContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
                recyclerView.setAdapter(searchUserRecyclerAdapter);
            }

            @Override
            public void onError(VolleyError error) {
                // Handle the error here
                Toast.makeText(SearchUserActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
