package edu.eci.dosw.tdd.core.model;

import lombok.Data;

@Data
public class Book {
    private String title;
    private String author;
    private String Id;

    public Book(String title, String author, String Id) {
        this.title = title;
        this.author = author;
        this.Id = Id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }
}
