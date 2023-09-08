package org.top.librarymvcapp.service;

import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.Genre;

import java.util.Optional;

@Service
public interface GenreService {
    Genre add(Genre genre);  // новый жанр
    Optional<Genre> getById(Integer id);     // получить жанр по id
    Iterable<Genre> getAll();      // получить все жанры
    void deleteById(Integer id);   // удалить жанр по id
    Genre update(Genre genre);   // изменить жанр по id
}
