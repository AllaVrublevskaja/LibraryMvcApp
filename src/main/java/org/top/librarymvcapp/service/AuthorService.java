package org.top.librarymvcapp.service;

import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.Author;

import java.util.Optional;

@Service
public interface AuthorService {
    Author add(Author author);                      // добавить автора
    Iterable<Author> getAll();                      // получить всех авторов
    Optional<Author> getById(Integer id);           // получить автора по id
    void deleteById(Integer id);                    // удалить автора по id
    Author update(Author author);                   // добавить автора
    Optional<Author> getByLastName(String name);
    String getShortName(Author author);
}
