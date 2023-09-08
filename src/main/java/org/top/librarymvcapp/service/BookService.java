package org.top.librarymvcapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.top.librarymvcapp.controller.form.BookForm;
import org.top.librarymvcapp.controller.form.FindBook;
import org.top.librarymvcapp.controller.form.InventoryForm;
import org.top.librarymvcapp.entity.Book;
import org.top.librarymvcapp.entity.BookAuthor;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface BookService {
    Book add(BookForm bookForm, MultipartFile file) throws IOException;
    BookForm addAuthor(BookForm bookForm, MultipartFile file) throws IOException;
    Book addForm(BookForm bookForm );
    Book getReturn(Book book);
    Iterable<Book> getAll();                          // получить все книги
    Optional<Book> getById(Integer id);           // получить книгу по id
    void deleteById(Integer id);                  // удалить книгу по id
    Book update(BookForm bookForm, MultipartFile file) throws IOException;
    BookForm updateForm(Optional<Book> bookUp, Set<BookAuthor> bookAuthors);
    void inventory(InventoryForm inventoryForm) throws ParseException;
    InventoryForm clearInventory(InventoryForm inventoryForm);
    List<Book> getFindBook(FindBook findBook);
}
