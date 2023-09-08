package org.top.librarymvcapp.rdb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.Author;
import org.top.librarymvcapp.rdb.repository.AuthorRepository;
import org.top.librarymvcapp.service.AuthorService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DbAuthorService implements AuthorService {
    private final AuthorRepository authorRepository;
    @Override
    public Author add(Author author) {
        if ((author.getId() != null && authorRepository.findById(author.getId()).isPresent()) ||
                authorRepository.findByLastName(author.getShortName()).isPresent()) {
            return null;
        }
        return authorRepository.save(author);
    }

    @Override
    public Iterable<Author> getAll() {
        var authors = authorRepository.findAll();
        if (authors.iterator().hasNext()) {
            return authors;
        }
        return null;
    }

    @Override
    public Optional<Author> getById(Integer id) {
        return authorRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) { authorRepository.deleteById(id);}

    @Override
    public Author update(Author author) {
        if (authorRepository.findById(author.getId()).isEmpty()) {
            return null;
        }
        return authorRepository.save(author);
    }

    @Override
    public Optional<Author> getByLastName(String lastName) {return authorRepository.findByLastName(lastName);}

    @Override
    public String getShortName(Author author) {
        return author.getShortName();
    }
}
