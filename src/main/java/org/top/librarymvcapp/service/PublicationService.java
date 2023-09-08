package org.top.librarymvcapp.service;

import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.Publication;

import java.util.Optional;

@Service
public interface PublicationService {
    Publication add(Publication publication);      // добавить новое издательство
    Optional<Publication> getById(Integer id);     // получить издательство по id
    Iterable<Publication> getAll();                // получить все издательства
    void deleteById(Integer id);                   // удалить издательство по id
    Publication update(Publication publication);   // изменить издательства
}
