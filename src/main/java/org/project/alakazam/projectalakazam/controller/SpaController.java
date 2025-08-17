package org.project.alakazam.projectalakazam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    /**
     * Forwards any path that is not handled by other controllers and does not contain a file extension.
     * The regular expression `{path:[^\\.]*}'` matches any path that does not contain a dot.
     * @return A forward instruction to the root path.
     */
    @GetMapping("/suggestions")
    public String suggestions() {
        return "suggestions.html";
    }

    @RequestMapping(value = "/{path:(?!suggestions$)[^\\.]*}")
    public String forward() {
        return "forward:/";
    }
}
