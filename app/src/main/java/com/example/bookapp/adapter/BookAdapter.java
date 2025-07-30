package com.example.bookapp.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookapp.R;
import com.example.bookapp.models.Book;
import com.example.bookapp.ui.UpdateBookFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;
    private OnBookClickListener listener;
    private DatabaseReference databaseReference;


    public BookAdapter(List<Book> books, OnBookClickListener listener) {
        this.books = books;
        this.listener = listener;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Books");
    }

    // Interface pour gérer les clics sur les livres
    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.categoryTextView.setText(book.getCategory());

        // Afficher la date de publication
        if (book.getPublicationDate() != null && !book.getPublicationDate().isEmpty()) {
            holder.publicationDateTextView.setText(book.getPublicationDate());
        }

        // Charger l'image (si disponible)
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Glide.with(holder.bookImageView.getContext())
                    .load(book.getImageUrl())
                    .into(holder.bookImageView);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });

        // Clic sur le bouton de modification
        holder.updateButton.setOnClickListener(v -> {
            // Implémenter la logique de modification
            updateBook(book, holder);
        });

        // Clic sur le bouton de suppression
        holder.deleteButton.setOnClickListener(v -> {
            // Implémenter la logique de suppression
            deleteBook(book, holder);
        });
    }

    public void updateBooks(List<Book> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }

    private void updateBook(Book book, BookViewHolder holder) {
        UpdateBookFragment updateFragment = UpdateBookFragment.newInstance(book);

        FragmentActivity activity = (FragmentActivity) holder.itemView.getContext();

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, updateFragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteBook(Book book, BookViewHolder holder) {
        if (book.getId() == null || book.getId().isEmpty()) {
            Log.e("DeleteBook", "ID du livre est vide ou nul !");
            Toast.makeText(holder.itemView.getContext(), "Erreur : ID du livre invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("DeleteBook", "Tentative de suppression du livre avec ID : " + book.getId());

        databaseReference.child(book.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    books.remove(book);
                    notifyDataSetChanged();
                    Toast.makeText(holder.itemView.getContext(), "Livre supprimé", Toast.LENGTH_SHORT).show();
                    Log.d("DeleteBook", "Livre supprimé avec succès !");
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteBook", "Erreur lors de la suppression : " + e.getMessage());
                    Toast.makeText(holder.itemView.getContext(), "Échec de la suppression", Toast.LENGTH_SHORT).show();
                });
    }

    private void refreshBooksList() {
        // Récupérer à nouveau tous les livres depuis Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                books.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    books.add(book);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("RefreshBooksList", "Erreur lors de la récupération des livres: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView, categoryTextView, publicationDateTextView;
        ImageView bookImageView;
        Button updateButton, deleteButton;

        public BookViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            authorTextView = itemView.findViewById(R.id.textViewAuthor);
            categoryTextView = itemView.findViewById(R.id.textViewCategory);
            bookImageView = itemView.findViewById(R.id.imageViewBook);
            publicationDateTextView = itemView.findViewById(R.id.textViewPublicationDate);
            updateButton = itemView.findViewById(R.id.updateButton); // Initialisation du bouton de mise à jour
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
