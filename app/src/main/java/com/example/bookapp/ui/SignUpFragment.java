package com.example.bookapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bookapp.R;
import com.example.bookapp.databinding.FragmentLoginBinding;
import com.example.bookapp.databinding.FragmentSignUpBinding;
import com.example.bookapp.models.User;
import com.example.bookapp.viewmodels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private EditText emailEditText, passwordEditText;
    private Spinner roleSpinner;
    private Button signUpButton;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        view.findViewById(R.id.loginText).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        signUpButton = view.findViewById(R.id.signUpButton);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        signUpButton.setOnClickListener(v -> signUpUser());

        return view;
    }

    private void signUpUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        User user = new User(userId, email, "user");

                        usersRef.child(userId).setValue(user).addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Sign up successful", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new LoginFragment()) // Remplacer par l'admin version de la BookListFragment
                                        .addToBackStack(null)
                                        .commit();

                            } else {
                                Toast.makeText(getContext(), "Failed to save user: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

