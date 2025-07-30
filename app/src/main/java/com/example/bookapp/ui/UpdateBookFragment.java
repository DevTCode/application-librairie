package com.example.bookapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookapp.R;
import com.example.bookapp.models.Book;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateBookFragment extends Fragment {
    // Clés pour les arguments
    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_AUTHOR = "author";
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_PUBLICATION_DATE = "publication_date";

    private ImageView imageViewBook;
    private EditText editTextImageUrl, editTextTitle, editTextAuthor, editTextCategory,
            editTextPublicationDate, editTextDescription;
    private Button buttonPreviewImage, buttonUpdateBook, buttonCancel;

    private DatabaseReference databaseReference;
    private String bookId;

    public static UpdateBookFragment newInstance(Book book) {
        UpdateBookFragment fragment = new UpdateBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, book.getId());
        args.putString(ARG_TITLE, book.getTitle());
        args.putString(ARG_AUTHOR, book.getAuthor());
        args.putString(ARG_CATEGORY, book.getCategory());
        args.putString(ARG_DESCRIPTION, book.getDescription());
        args.putString(ARG_IMAGE_URL, book.getImageUrl());
        args.putString(ARG_PUBLICATION_DATE, book.getPublicationDate());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookId = getArguments().getString(ARG_BOOK_ID);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_book, container, false);

        initializeViews(view);
        setupListeners();
        populateFields();

        return view;
    }

    private void initializeViews(View view) {
        imageViewBook = view.findViewById(R.id.imageViewUpdateBook);
        editTextImageUrl = view.findViewById(R.id.editTextImageUrl);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextAuthor = view.findViewById(R.id.editTextAuthor);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextPublicationDate = view.findViewById(R.id.editTextPublicationDate);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonUpdateBook = view.findViewById(R.id.buttonUpdateBook);
        buttonCancel = view.findViewById(R.id.buttonCancel);
    }

    private void setupListeners() {
        buttonUpdateBook.setOnClickListener(v -> updateBook());
        buttonCancel.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void populateFields() {
        Bundle args = getArguments();
        if (args != null) {
            editTextTitle.setText(args.getString(ARG_TITLE));
            editTextAuthor.setText(args.getString(ARG_AUTHOR));
            editTextCategory.setText(args.getString(ARG_CATEGORY));
            editTextPublicationDate.setText(args.getString(ARG_PUBLICATION_DATE));
            editTextDescription.setText(args.getString(ARG_DESCRIPTION));
            String imageUrl = args.getString(ARG_IMAGE_URL);
            editTextImageUrl.setText(imageUrl);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                loadImage(imageUrl);
            }
        }
    }

    private void previewImage() {
        String imageUrl = editTextImageUrl.getText().toString().trim();
        if (!imageUrl.isEmpty()) {
            loadImage(imageUrl);
        } else {
            Toast.makeText(requireContext(), "Veuillez entrer une URL d'image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage(String imageUrl) {
        Glide.with(requireContext())
                .load(imageUrl)
                .error(R.drawable.ic_launcher_background) // Image par défaut en cas d'erreur
                .into(imageViewBook);
    }

    private boolean validateFields() {
        if (editTextTitle.getText().toString().trim().isEmpty()) {
            editTextTitle.setError("Le titre est requis");
            return false;
        }
        if (editTextAuthor.getText().toString().trim().isEmpty()) {
            editTextAuthor.setError("L'auteur est requis");
            return false;
        }
        return true;
    }

    private void updateBook() {
        if (!validateFields()) return;

        Book updatedBook = new Book();
        updatedBook.setId(bookId);
        updatedBook.setTitle(editTextTitle.getText().toString().trim());
        updatedBook.setAuthor(editTextAuthor.getText().toString().trim());
        updatedBook.setCategory(editTextCategory.getText().toString().trim());
        updatedBook.setPublicationDate(editTextPublicationDate.getText().toString().trim());
        updatedBook.setDescription(editTextDescription.getText().toString().trim());
        updatedBook.setImageUrl(editTextImageUrl.getText().toString().trim());

        databaseReference.child(bookId).setValue(updatedBook)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Livre mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Échec de la mise à jour", Toast.LENGTH_SHORT).show();
                });
    }
}