package com.example.bookapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookapp.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookViewModel extends ViewModel {

    private MutableLiveData<List<Book>> booksLiveData;
    private MutableLiveData<Boolean> isBookAdded;
    private DatabaseReference databaseReference;

    public BookViewModel() {
        booksLiveData = new MutableLiveData<>();
        isBookAdded = new MutableLiveData<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        loadBooks();
    }

    private void loadBooks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Book> books = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null) {
                        book.setId(snapshot.getKey());  // Ajoutez cette ligne pour sauvegarder l'ID
                        books.add(book);
                    }
                }
                booksLiveData.setValue(books);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gérer les erreurs si nécessaire
            }
        });
    }

    public LiveData<List<Book>> getBooks() {
        return booksLiveData;
    }

    public LiveData<Boolean> isBookAdded() {
        return isBookAdded;
    }

    // Méthode pour ajouter un livre
    public void addBook(Book book) {
        String bookId = databaseReference.push().getKey();
        if (bookId != null) {
            databaseReference.child(bookId).setValue(book).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    isBookAdded.setValue(true);
                } else {
                    isBookAdded.setValue(false);
                }
            });
        }
    }

}
