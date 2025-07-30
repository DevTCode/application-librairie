package com.example.bookapp.repository;

import com.example.bookapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRepository {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public void signUp(String email, String password, String role, Callback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = auth.getCurrentUser().getUid();
                User user = new User(userId, email, "user");
                usersRef.child(userId).setValue(user)
                        .addOnSuccessListener(unused -> callback.onSuccess(user))
                        .addOnFailureListener(callback::onFailure);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    public void login(String email, String password, Callback<User> callback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = auth.getCurrentUser().getUid();
                usersRef.child(userId).get().addOnSuccessListener(snapshot -> {
                    User user = snapshot.getValue(User.class);
                    callback.onSuccess(user);
                }).addOnFailureListener(callback::onFailure);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }
}
