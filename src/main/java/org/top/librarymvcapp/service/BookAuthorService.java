package org.top.librarymvcapp.service;


import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.BookAuthor;

@Service
public interface BookAuthorService {
    BookAuthor update(BookAuthor bookAuthor);
}
