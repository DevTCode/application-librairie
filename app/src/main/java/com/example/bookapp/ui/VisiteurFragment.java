package com.example.bookapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookapp.R;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapter.BookAdapter;
import com.example.bookapp.adapter.BookUVAdapter;
import com.example.bookapp.models.Book;
import com.example.bookapp.viewmodels.BookViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class VisiteurFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookUVAdapter bookAdapter;
    private BookViewModel bookViewModel;
    private EditText searchEditText;
    private Spinner categorySpinner;
    private List<Book> allBooks = new ArrayList<>();
    private List<Book> filteredBooks = new ArrayList<>();
    private DatabaseReference categoryRef;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visiteur, container, false);

        // Initialiser Firebase pour récupérer les catégories
        categoryRef = FirebaseDatabase.getInstance().getReference("Books");

        // Initialiser RecyclerView et Adapter
        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        bookAdapter = new BookUVAdapter(new ArrayList<>(), book -> {
            // Naviguer vers le fragment d'affichage de la description
            Bundle bundle = new Bundle();
            bundle.putString("bookTitle", book.getTitle());
            bundle.putString("bookDescription", book.getDescription());

            DescriptionFragment descriptionFragment = new DescriptionFragment();
            descriptionFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, descriptionFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(bookAdapter);

        // Initialiser ViewModel pour récupérer les livres
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.getBooks().observe(getViewLifecycleOwner(), books -> {
            if (books != null && !books.isEmpty()) {
                allBooks = books;
                filteredBooks.addAll(books);
                bookAdapter.updateBooks(filteredBooks);
            } else {
                Toast.makeText(getContext(), "Aucun livre disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialiser le champ de recherche
        searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterBooks();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Initialiser le Spinner pour les catégories
        categorySpinner = view.findViewById(R.id.categorySpinner);

        categoryRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> categories = new ArrayList<>();
                categories.add("Toutes les catégories");

                // Ajouter toutes les catégories uniques depuis Firebase
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String category = snapshot.child("category").getValue(String.class);
                    if (category != null && !categories.contains(category)) {
                        categories.add(category);
                    }
                }

                // Mettre à jour l'adaptateur du Spinner avec les catégories
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterBooks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        return view;
    }

    // Filtrer les livres en fonction de la recherche et de la catégorie
    private void filterBooks() {
        String searchQuery = searchEditText.getText().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem() != null
                ? categorySpinner.getSelectedItem().toString()
                : "Toutes les catégories";

        filteredBooks.clear();
        for (Book book : allBooks) {
            if (book != null) {
                String bookTitle = book.getTitle() != null ? book.getTitle().toLowerCase() : "";
                String bookCategory = book.getCategory() != null ? book.getCategory() : "";

                boolean matchesSearch = bookTitle.contains(searchQuery);
                boolean matchesCategory = selectedCategory.equals("Toutes les catégories") || bookCategory.equals(selectedCategory);

                if (matchesSearch && matchesCategory) {
                    filteredBooks.add(book);
                }
            }
        }
        bookAdapter.updateBooks(filteredBooks);
    }
}