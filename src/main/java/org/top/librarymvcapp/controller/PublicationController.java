package org.top.librarymvcapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.librarymvcapp.entity.Publication;
import org.top.librarymvcapp.service.PublicationService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("public")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationSer;

    @GetMapping("")
    public String publicationList(Model model){
        List<Publication> publications = (List<Publication>) publicationSer.getAll();
        publications.sort(Comparator.comparing(Publication::getName));
        model.addAttribute("publications", publications);
        return "public/publication-list";
    }
    @GetMapping("new")
    public String newPublication(Model model){
        Publication newPublication = new Publication();
        model.addAttribute("newPublication", newPublication);
        return "public/publication-add-form";
    }

    @PostMapping("new")
    public String newPublication(Publication newPublication){
        Publication publication = publicationSer.add(newPublication);
        if (publication!=null) return "redirect:/public/new";
        return "redirect:/public";
    }

    @GetMapping("delete/{id}")
    public String deletePublication(@PathVariable Integer id){
        Optional<Publication> deleted = publicationSer.getById(id);
        if(deleted.isPresent()){
            publicationSer.deleteById(id);
        }
        return "redirect:/public";
    }

    @GetMapping("details/{id}")
    public String detailsGenre(@RequestParam(required = false) String anchor,
                               @PathVariable Integer id, Model model) {
        Optional<Publication> publication = publicationSer.getById(id);
        if (publication.isPresent()) {
            if(anchor!=null)
                model.addAttribute("anchor",anchor);
            model.addAttribute("publication", publication.get());
            return "public/publication-details";
        }
        return "redirect:/public";
    }

    @GetMapping("update/{id}")
    public String updateGenre(@PathVariable Integer id, Model model) {
        // обновляемый объект для заполнения на форме
        Optional<Publication> publication = publicationSer.getById(id);
        if (publication.isPresent()) {
            model.addAttribute("publication", publication);
            return "public/publication-update-form";
        }
        return "redirect:/public";
    }

    @PostMapping("update")
    public String updatePublic(Publication updatedPublic) {
        publicationSer.update(updatedPublic);
        return "redirect:/public";
    }
}
