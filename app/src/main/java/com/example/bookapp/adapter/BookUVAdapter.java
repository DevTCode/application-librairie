package com.example.bookapp.adapter;

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
import com.example.bookapp.ui.HomeFragment;
import com.example.bookapp.ui.UpdateBookFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BookUVAdapter extends RecyclerView.Adapter<BookUVAdapter.BookViewHolder> {
    private List<Book> books;
    private BookAdapter.OnBookClickListener listener;
    private DatabaseReference databaseReference;
    private DatabaseReference favoritesRef;
    private FirebaseAuth mAuth;

    public BookUVAdapter(List<Book> books, BookAdapter.OnBookClickListener listener) {
        this.books = books;
        this.listener = listener;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        this.favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites");
        this.mAuth = FirebaseAuth.getInstance();
    }

    // Interface pour gérer les clics sur les livres
    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    @NonNull
    @Override
    public BookUVAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_uv, parent, false);
        return new BookUVAdapter.BookViewHolder(view);
    }

    public void updateBooks(List<Book> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
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

        // Vérifier si l'utilisateur est connecté
        String userId;
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        } else {
            userId = null;
            // Si l'utilisateur n'est pas connecté, on fait ce qui suit
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty); // Icône pour un visiteur
            holder.favoriteButton.setOnClickListener(v -> {
                // Ici on pourrait rediriger vers un fragment ou une activité différente
                // Par exemple, redirection vers un écran de connexion ou un fragment Home pour les visiteurs
                FragmentActivity activity = (FragmentActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()) // Par exemple, affichage d'un fragment d'accueil
                        .addToBackStack(null)
                        .commit();
                Toast.makeText(v.getContext(), "Vous devez être connecté pour ajouter aux favoris.", Toast.LENGTH_SHORT).show();
            });
            return;
        }

        // Si l'utilisateur est connecté, on affiche les favoris spécifiques à cet utilisateur
        favoritesRef.child(userId).child(book.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled); // Livre favori
                } else {
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty); // Livre non favori
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Ajouter au favoris si l'utilisateur est connecté
        holder.favoriteButton.setOnClickListener(v -> {
            // Ajouter le livre aux favoris de l'utilisateur connecté
            favoritesRef.child(userId).child(book.getId()).setValue(book)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(holder.itemView.getContext(), "Livre ajouté aux favoris", Toast.LENGTH_SHORT).show();
                        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
                    })
                    .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Échec de l'ajout aux favoris", Toast.LENGTH_SHORT).show());
        });

        // Gérer le clic sur un livre pour ouvrir un fragment ou une nouvelle activité
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });
    }


    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView, categoryTextView, publicationDateTextView;
        ImageView bookImageView, favoriteButton;

        public BookViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            authorTextView = itemView.findViewById(R.id.textViewAuthor);
            categoryTextView = itemView.findViewById(R.id.textViewCategory);
            bookImageView = itemView.findViewById(R.id.imageViewBook);
            publicationDateTextView = itemView.findViewById(R.id.textViewPublicationDate);
            favoriteButton = itemView.findViewById(R.id.favoriteButton); // Icône "J'aime"
        }
    }
}
