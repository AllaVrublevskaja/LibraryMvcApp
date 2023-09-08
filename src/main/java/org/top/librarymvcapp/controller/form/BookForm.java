package org.top.librarymvcapp.controller.form;

import jakarta.persistence.Lob;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.top.librarymvcapp.entity.Author;

import java.util.List;

// форма для добавления и обновления сущности
@Data
@NoArgsConstructor
public class BookForm {
    private Integer id;
    private String code;
    private String title;
    private Integer year;
    private Integer pages;
    private Integer quantityInstances;
    private Integer genreId;
    private Integer publicationId;
    private Integer authorId;
    private List<Author> selects;
    private Integer loop=1;
    // файл в форме
    @Lob
    private String imageFile;
}
