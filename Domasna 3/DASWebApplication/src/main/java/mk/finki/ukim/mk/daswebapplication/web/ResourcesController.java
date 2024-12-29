package mk.finki.ukim.mk.daswebapplication.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/resources")
public class ResourcesController {

    @GetMapping
    public String resources() {
        return "resources";
    }
}
