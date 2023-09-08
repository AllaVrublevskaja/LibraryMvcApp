package org.top.librarymvcapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="book_author_t")
@Data
public class BookAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id",nullable = false)
    Book book;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    Author author;

    @Override
    public String toString() {
        return id + " - " + book.getId() + " - " + book.getName() + " - " +
                author.getId() + " - " + author.getLastName() + " " +
                author.getFirstName() + " " + author.getPatronymic();
    }
}
