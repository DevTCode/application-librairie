package com.example.bookapp.models;

public class Book {
    private String id;
    private String title;
    private String author;
    private String category;
    private String description;
    private String imageUrl; // Ajout de l'URL de l'image
    private String publicationDate;

    public Book() {
    }

    // Constructeur
    public Book(String id,String title, String author, String category, String description, String imageUrl,String publicationDate) {
        this.id=id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
        this.publicationDate=publicationDate;
    }

    // Getters et Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }
}
