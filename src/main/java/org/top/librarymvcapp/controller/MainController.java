package org.top.librarymvcapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @GetMapping("")
    public String index() {
        return "index";
    }

    @PostMapping("")
    public String postIndex() {
        return "redirect:/";
    }

    @GetMapping("ping")
    public String ping() {
        return "ping";
    }
}
