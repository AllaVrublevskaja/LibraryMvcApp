package org.top.librarymvcapp.rdb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.top.librarymvcapp.entity.Publication;

import java.util.Optional;

@Repository
public interface PublicationRepository extends CrudRepository<Publication, Integer> {
    Optional<Publication> findByName(String name);
}
