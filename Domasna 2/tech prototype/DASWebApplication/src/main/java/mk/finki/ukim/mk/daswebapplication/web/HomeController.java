package mk.finki.ukim.mk.daswebapplication.web;


import mk.finki.ukim.mk.daswebapplication.service.CsvService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final CsvService csvService;

    public HomeController(CsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping
    public String homepage(Model model) throws IOException {
        List<String> symbols = csvService.getAvailableSymbols();
        model.addAttribute("symbols", symbols);
        return "home";
    }
}
