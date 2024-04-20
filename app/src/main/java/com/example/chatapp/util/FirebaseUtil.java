package com.example.chatapp.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static String currentUserUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isUserLoggedIn() {
        return currentUserUid() != null;
    }
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserUid());
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }
}
