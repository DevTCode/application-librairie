package com.example.bookapp.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookapp.R;
import com.example.bookapp.models.Book;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.UUID;

public class AddBookFragment extends Fragment {
    private EditText titleEditText, authorEditText, categoryEditText, descriptionEditText, imageUrlEditText, publicationDateEditText,editTextPublicationDate;
    private View addBookButton;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Gonflez le layout pour ce fragment
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        // Initialisez les vues
        titleEditText = view.findViewById(R.id.titleEditText);
        authorEditText = view.findViewById(R.id.authorEditText);
        categoryEditText = view.findViewById(R.id.categoryEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        imageUrlEditText = view.findViewById(R.id.imageUrlEditText);
        editTextPublicationDate = view.findViewById(R.id.editTextPublicationDate);
        addBookButton = view.findViewById(R.id.addBookButton); // Assurez-vous que l'ID correspond

        // Définissez un OnClickListener pour le bouton
        addBookButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String author = authorEditText.getText().toString();
            String category = categoryEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String imageUrl = imageUrlEditText.getText().toString();
            String publicationDate = editTextPublicationDate.getText().toString(); // Récupérer la date de publication

            // Vérifiez que les champs obligatoires sont remplis
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author) && !TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(publicationDate)) {

                String bookId = UUID.randomUUID().toString();

                // Créer un objet Book avec les données saisies
                Book book = new Book(bookId, title, author, category, description, imageUrl, publicationDate);

                // Ajouter le livre à Firebase
                addBookToFirebase(book);
            } else {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
        publicationDateEditText = view.findViewById(R.id.editTextPublicationDate);



        return view;
    }

    // Méthode pour ajouter le livre à Firebase
    private void addBookToFirebase(Book book) {
        FirebaseDatabase.getInstance().getReference("Books")
                .push()
                .setValue(book)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Book added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to add book", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
