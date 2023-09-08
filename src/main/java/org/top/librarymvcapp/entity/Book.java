package org.top.librarymvcapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="book_t")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="code_f", nullable = false)
    private String code;

    @Column(name="name_f", nullable = false)
    private String name;

    @Column(name="year_release_f")
    private Integer yearRelease;

    @Column(name="quantity_pages_f")
    private Integer quantityPages;

    @Column(name="quantity_instances_f")
    private Integer quantityInstances;

    @Column(name="quantity_stock_f")
    private Integer quantityStock;

    @Column(name="creation_date_f")
    private Date creationDate;

    @Column(name="handedout_f")
    private Integer handedOut;

    @Column(name="quantity_inventory_f")
    private Integer quantityInventory;

    @Column(name="date_inventory_f")
    private Date dateInventory;

    @Column(columnDefinition = "TEXT")
    private String imageFile;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    Set<BookAuthor> bookAuthors;

    @ManyToOne
    @JoinColumn(name = "genre_id",nullable = false)
    Genre genre;

    @ManyToOne
    @JoinColumn(name = "publication_id",nullable = false)
    Publication publication;

    // вспомогтальеные методы для работы с датой
    public String creationDateFormatted() {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        if(creationDate==null) return null;
        return formatter.format(creationDate);
    }
    public String dateInventoryFormatted() {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        if(dateInventory==null) return null;
        return formatter.format(dateInventory);
    }
    public int maxYear() {
        LocalDate today = LocalDate.now();
        return today.getYear();
    }

    @Override
    public String toString() {
        return id + " - " + name + " - " + yearRelease + " - " +
                quantityPages + " - " + quantityInstances + " - " +
                quantityInventory + " - " + dateInventory + " - " +
                quantityStock + " - " + handedOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(name, book.name) &&
                Objects.equals(yearRelease, book.yearRelease) &&
                Objects.equals(quantityPages, book.quantityPages) &&
                Objects.equals(quantityInstances, book.quantityInstances) &&
                Objects.equals(quantityInventory, book.quantityInventory) &&
                Objects.equals(quantityStock, book.quantityStock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, yearRelease, quantityPages,
                quantityInstances,quantityInventory, quantityStock);
    }
}
