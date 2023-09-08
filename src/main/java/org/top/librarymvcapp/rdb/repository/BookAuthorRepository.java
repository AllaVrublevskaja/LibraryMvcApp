package org.top.librarymvcapp.rdb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.top.librarymvcapp.entity.BookAuthor;

@Repository
public interface BookAuthorRepository extends CrudRepository <BookAuthor, Integer>{
void deleteAllByBookId(Integer bookId);
}
