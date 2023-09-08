package org.top.librarymvcapp.rdb.repository;

import org.springframework.data.repository.CrudRepository;
import org.top.librarymvcapp.entity.Author;

import java.util.Optional;

public interface AuthorRepository extends CrudRepository<Author, Integer> {
        Optional<Author> findByLastName(String name);
}
