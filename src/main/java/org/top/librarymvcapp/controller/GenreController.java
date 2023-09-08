package org.top.librarymvcapp.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.librarymvcapp.entity.Genre;
import org.top.librarymvcapp.service.GenreService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@Transactional
@RequestMapping("genre")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("")
    public String genreList(Model model){
        List<Genre> genres = (List<Genre>) genreService.getAll();
        genres.sort(Comparator.comparing(Genre::getName));
        model.addAttribute("genres", genres);
        return "genre/genre-list";
    }

    @GetMapping("new")
    public String newGenre(Model model){
        Genre newGenre = new Genre();
        model.addAttribute("newGenre", newGenre);
        return "genre/genre-add-form";
    }

    @PostMapping("new")
    public String newGenre(Genre newGenre){
        Genre genre = genreService.add(newGenre);
        if (genre!=null) return "redirect:/genre/new";
        return "redirect:/genre";
    }

    @GetMapping("delete/{id}")
    public String deleteGenre(@PathVariable Integer id){
        Optional<Genre> deleted = genreService.getById(id);
        String genreName = "";
        if(deleted.isPresent()){
            genreService.deleteById(id);
        }
        return "redirect:/genre";
    }
    // обработчик details (аналог получения по id)
    @GetMapping("details/{id}")
    public String detailsGenre(@RequestParam(required = false) String anchor,
                               @PathVariable Integer id, Model model) {
        Optional<Genre> genre = genreService.getById(id);
        if (genre.isPresent()) {
            if(anchor!=null)
                model.addAttribute("anchor",anchor);
            model.addAttribute("genre", genre.get());
            return "genre/genre-details";
        }
        return "redirect:/genre";
    }

    @GetMapping("update/{id}")
    public String updateGenre(@PathVariable Integer id, Model model) {
        // обновляемый объект для заполнения на форме
        Optional<Genre> genre = genreService.getById(id);
        if (genre.isPresent()) {
            model.addAttribute("genre", genre);
            return "genre/genre-update-form";
        }
        return "redirect:/genre";
    }

    @PostMapping("update")
    public String updateGenre(Genre updatedGenre) {
        genreService.update(updatedGenre);
        return "redirect:/genre";
    }
}
