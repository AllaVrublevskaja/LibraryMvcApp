package org.top.librarymvcapp.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.top.librarymvcapp.entity.Book;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBook {
    Integer page;
    Integer records;
    Integer bookNumber;
    Integer pages;
    Integer size;
    List<Book> userBooks;
    List<Book> books;
}
