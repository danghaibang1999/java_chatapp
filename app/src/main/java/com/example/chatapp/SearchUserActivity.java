package com.example.chatapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.SearchUserRecyclerAdapter;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

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
            String searchTerm = searchInput.getText().toString();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        }));
    }

    void setupSearchRecyclerView(String searchTerm) {

        Query query = FirebaseUtil.allUserCollectionReference().whereGreaterThanOrEqualTo("username", searchTerm);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query, UserModel.class).build();

        searchUserRecyclerAdapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchUserRecyclerAdapter);
        searchUserRecyclerAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (searchUserRecyclerAdapter != null) {
            searchUserRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (searchUserRecyclerAdapter != null) {
            searchUserRecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchUserRecyclerAdapter != null) {
            searchUserRecyclerAdapter.notifyDataSetChanged();
        }
    }
}