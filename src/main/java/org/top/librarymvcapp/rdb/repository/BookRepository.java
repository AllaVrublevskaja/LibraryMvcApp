package org.top.librarymvcapp.rdb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.top.librarymvcapp.entity.Book;

@Repository
public interface BookRepository extends CrudRepository <Book, Integer> {
}
