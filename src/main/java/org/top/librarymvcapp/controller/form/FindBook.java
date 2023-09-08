package org.top.librarymvcapp.controller.form;

import lombok.Data;

import java.util.Objects;

@Data
public class FindBook {
    private String title;
    private String author;
    private String code;
    private Integer genreId;
    private Integer publicationId;

    public FindBook() {
        title = author = code = "";
    }

    public boolean isEmpty() {
        return Objects.equals(title, "") &&
                Objects.equals(author, "") &&
                Objects.equals(code, "") ;
    }
}
