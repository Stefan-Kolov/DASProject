package mk.finki.ukim.mk.daswebapplication.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/market-trends")
public class MarketTrendsController {

    @GetMapping
    public String home() {
        return "market-trends";
    }
}
