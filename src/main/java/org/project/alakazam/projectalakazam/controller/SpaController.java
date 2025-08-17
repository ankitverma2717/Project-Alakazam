package org.project.alakazam.projectalakazam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * This controller forwards all non-API, non-static file requests to the root path ("/"),
 * allowing the Next.js Single Page Application (SPA) to handle its own routing.
 */
@Controller
public class SpaController {

    @GetMapping("/suggestions")
    public String suggestions() {
        return "redirect:/suggestions.html";
    }

    /**
     * Forwards any path that is not a static file (e.g., .js, .css) and is not
     * handled by another specific controller (like /graphql).
     * @return A forward instruction to the root path, which serves the index.html.
     */
    @RequestMapping(value = "/{path:(?!suggestions$)[^\\.]*}")
    public String forward() {
        return "forward:/";
    }
}