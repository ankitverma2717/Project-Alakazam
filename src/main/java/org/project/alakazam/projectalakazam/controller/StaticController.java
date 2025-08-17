package org.project.alakazam.projectalakazam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    @GetMapping("/suggestions")
    public String suggestions() {
        return "suggestions.html";
    }
}