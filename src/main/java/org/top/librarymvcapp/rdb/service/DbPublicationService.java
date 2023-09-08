package org.top.librarymvcapp.rdb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.Publication;
import org.top.librarymvcapp.rdb.repository.PublicationRepository;
import org.top.librarymvcapp.service.PublicationService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DbPublicationService implements PublicationService {

    private final PublicationRepository publicationRepository;

    @Override
    public Publication add(Publication publication) {
        if ((publication.getId() != null && publicationRepository
                .findById(publication.getId()).isPresent()) ||
                publicationRepository.findByName(
                        publication.getName()).isPresent()) {
            return null;    // издательство не добавили
        }
        return publicationRepository.save(publication);
    }

    @Override
    public Optional<Publication> getById(Integer id) {
        return publicationRepository.findById(id);
    }

    @Override
    public Iterable<Publication> getAll() {
        var publications = publicationRepository.findAll();
        if (publications.iterator().hasNext()) {
            return publications;
        }
        return null;
    }

    @Override
    public void deleteById(Integer id) {
        publicationRepository.deleteById(id);
    }

    @Override
    public Publication update(Publication publication) {
        if (publication.getId() == null) {
            return null;   //жанр не обновлен
        }
        return publicationRepository.save(publication);
    }
}
