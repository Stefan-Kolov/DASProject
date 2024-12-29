package mk.finki.ukim.mk.daswebapplication.web;

import mk.finki.ukim.mk.daswebapplication.model.StockData;
import mk.finki.ukim.mk.daswebapplication.service.CsvService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/trends")
public class TrendsController {
    private final CsvService csvService;

    public TrendsController(CsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping
    public String showMarketTrendsPage(Model model) throws IOException {
        List<StockData> topSymbols = csvService.getTop10SymbolsByLatestDateAndProfit();
        String date = topSymbols.getFirst().getDate().toString();
        model.addAttribute("topSymbols", topSymbols);
        model.addAttribute("lastDate", date);
        return "market-trends";
    }
}
