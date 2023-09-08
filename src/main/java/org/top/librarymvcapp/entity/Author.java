package org.top.librarymvcapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "author_t")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lastName_f", nullable = false)
    private String lastName;

    @Column(name="firstName_f")
    private String firstName;

    @Column(name="patronymic_f")
    private String patronymic;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    List<BookAuthor> bookAuthors;

    // Вспомогательные методы работы с именем (вычисляемые геттеры)
    public String getShortName() {
        String message = lastName;
        if(!firstName.isEmpty()) message = message + " " + firstName.charAt(0);
        if(!patronymic.isEmpty()) message = message + "." + patronymic.charAt(0) + ".";
        return message;
    }

    public String getFullName() {
        // TODO: учитывать пустые поля
        String message = lastName;
        if(!firstName.isEmpty()) message = message + " " + firstName;
        if(!patronymic.isEmpty()) message = message + " " + patronymic;
        return message;
    }

    @Override
    public String toString() {
        return id + " - " + getShortName();
    }
}
