package com.example.chatapp.util;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static String currentUserUid() {
        return firebaseAuth.getUid();
    }

    public static String currentUserName() {
        return firebaseAuth.getCurrentUser().getDisplayName();
    }

    public static boolean isUserLoggedIn() {
        return currentUserUid() != null;
    }

    public static DocumentReference currentUserDetails() {
        return firestore.collection("users").document(currentUserUid());
    }

    public static CollectionReference allUserCollectionReference() {
        return firestore.collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return firestore.collection("chatrooms").document(chatroomId);
    }

    public static String getChatroomId(String currentUserId, String otherUserId) {
        return currentUserId.compareTo(otherUserId) < 0 ?
                currentUserId + "_" + otherUserId : otherUserId + "_" + currentUserId;
    }

    public static CollectionReference getChatroomMessagesReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static CollectionReference allChatroomCollectionReference() {
        return firestore.collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        String otherUserId = userIds.get(0).equals(currentUserUid()) ? userIds.get(1) : userIds.get(0);
        return allUserCollectionReference().document(otherUserId);
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("hh:mm").format(timestamp.toDate());
    }

    public static void logout() {
        firebaseAuth.signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef() {
        return storage.getReference().child("profile_pics").child(currentUserUid());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return storage.getReference().child("profile_pics").child(otherUserId);
    }

    public static StorageReference getChatroomImageStorageRef(String chatroomId, String imageId) {
        return storage.getReference().child("chatroom_images").child(chatroomId).child(imageId);
    }
}
