package org.top.librarymvcapp.rdb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.BookAuthor;
import org.top.librarymvcapp.rdb.repository.BookAuthorRepository;
import org.top.librarymvcapp.service.BookAuthorService;

@Service
@RequiredArgsConstructor
public class DbBookAuthorService implements BookAuthorService {

    private final BookAuthorRepository bookAuthorRepository;

    @Override
    public BookAuthor update(BookAuthor bookAuthor) {
        if (bookAuthorRepository.findById(bookAuthor.getId()).isEmpty()) {
            return null;
        }
        return bookAuthorRepository.save(bookAuthor);
    }
}
