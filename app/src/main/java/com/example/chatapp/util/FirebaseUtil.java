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

    public static String currentUserUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static String currentUserName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
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

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static String getChatroomId(String currentUserId, String otherUserId) {
        if (currentUserId.compareTo(otherUserId) < 0) {
            return currentUserId + "_" + otherUserId;
        } else {
            return otherUserId + "_" + currentUserId;
        }
    }

    public static CollectionReference getChatroomMessagesReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(currentUserUid())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("hh:mm").format(timestamp.toDate());
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pics")
                .child(FirebaseUtil.currentUserUid());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return FirebaseStorage.getInstance().getReference().child("profile_pics")
                .child(otherUserId);
    }

    public static StorageReference getChatroomImageStorageRef(String chatroomId, String imageId) {
        return FirebaseStorage.getInstance().getReference().child("chatroom_images")
                .child(chatroomId).child(imageId);
    }
}
