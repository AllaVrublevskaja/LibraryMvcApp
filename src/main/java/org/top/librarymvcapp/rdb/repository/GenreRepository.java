package org.top.librarymvcapp.rdb.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.top.librarymvcapp.entity.Genre;

import java.util.Optional;

@Repository
public interface GenreRepository extends CrudRepository<Genre, Integer> {
  Optional<Genre> findByName(String name);
}
