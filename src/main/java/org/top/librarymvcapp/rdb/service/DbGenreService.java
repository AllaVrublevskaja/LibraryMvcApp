package org.top.librarymvcapp.rdb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.Genre;
import org.top.librarymvcapp.rdb.repository.GenreRepository;
import org.top.librarymvcapp.service.GenreService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DbGenreService implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public Genre add(Genre genre) {
        if ((genre.getId() != null && genreRepository.findById(genre.getId()).isPresent()) ||
                genreRepository.findByName(genre.getName()).isPresent()) {
            return null;    // жанр не добавили
        }
        return genreRepository.save(genre);
    }

    @Override
    public Optional<Genre> getById(Integer id) { return genreRepository.findById(id);}

    @Override
    public Iterable<Genre> getAll() {
        var genres = genreRepository.findAll();
        if (genres.iterator().hasNext()) {
            return genres;
        }
        return null;
    }

    @Override
    public void deleteById(Integer id) { genreRepository.deleteById(id);}

    @Override
    public Genre update(Genre genre) {
        if (genre.getId() == null) {
            return null;   //жанр не обновлен
        }
        return genreRepository.save(genre);
    }
}
