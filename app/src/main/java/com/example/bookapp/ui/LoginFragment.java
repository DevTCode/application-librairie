package com.example.bookapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookapp.R;
import com.example.bookapp.databinding.FragmentHomeBinding;
import com.example.bookapp.databinding.FragmentLoginBinding;
import com.example.bookapp.models.User;
import com.example.bookapp.viewmodels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.Nullable;
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        view.findViewById(R.id.signUpText).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SignUpFragment())
                    .addToBackStack(null)
                    .commit();
        });


        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loginButton.setOnClickListener(v -> loginUser());

        return view;
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        usersRef.child(userId).get().addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                User user = dbTask.getResult().getValue(User.class);
                                if (user != null) {
                                    if ("admin".equals(user.getRole())) {
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, new BookListFragment()) // Remplacer par l'admin version de la BookListFragment
                                                .addToBackStack(null)
                                                .commit();
                                    } else {
                                        Toast.makeText(getContext(), "User connected successfully", Toast.LENGTH_SHORT).show();

                                        // Rediriger vers la UserBookListFragment pour l'utilisateur normal
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, new UserBookListFragment()) // Remplacer par la version UserBookListFragment
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "Failed to fetch user role", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
