package org.top.librarymvcapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.librarymvcapp.entity.Author;
import org.top.librarymvcapp.service.AuthorService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("author")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("")
    public String authorList(Model model) {
        List<Author> authors = (List<Author>) authorService.getAll();
        authors.sort(Comparator.comparing(Author::getFullName));
        model.addAttribute("authors", authors);
        return "author/author-list";
    }

    @GetMapping("new")
    public String newAuthor(Model model){
        Author newAuthor = new Author();
        model.addAttribute("newAuthor", newAuthor);
        return "author/author-add-form";
    }

    @PostMapping("new")
    public String newAuthor(Author newAuthor){
        Author author = authorService.add(newAuthor);
        if (author != null)  return "redirect:/author/new";
        return "redirect:/author";
    }
    // обработчик details (аналог получения по id)
    @GetMapping("details/{id}")
    public String detailsClient(@RequestParam(required = false) String anchor,
                                @PathVariable Integer id, Model model) {
        Optional<Author> author = authorService.getById(id);
        if (author.isPresent()) {
            if(anchor!=null)
                model.addAttribute("anchor",anchor);
            model.addAttribute("author", author.get());
            return "author/author-details";
        }
        return "redirect:/author";
    }

    @GetMapping("delete/{id}")
    public String deleteAuthor(@PathVariable Integer id){
        Optional<Author> deleted = authorService.getById(id);
        String authorName = "";
        if(deleted.isPresent()){
            authorName =deleted.get().getFullName();
            authorService.deleteById(id);
        }
        return "redirect:/author";
    }

    @GetMapping("update/{id}")
    public String updateAuthor(@PathVariable Integer id, Model model) {
        // обновляемый объект для заполнения на форме
        Optional <Author> author = authorService.getById(id);
        if (author.isPresent()) {
            model.addAttribute("author", author.get());
            return "author/author-update-form";
        }
        return "redirect:/author";
    }

    @PostMapping("update")
    public String updateAuthor(Author updatedAuthor) {
        Author author = authorService.update(updatedAuthor);
        return "redirect:/author";
    }
}
