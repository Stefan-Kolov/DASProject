package mk.finki.ukim.mk.daswebapplication.web;

import mk.finki.ukim.mk.daswebapplication.model.StockData;
import mk.finki.ukim.mk.daswebapplication.service.CsvService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/market")
public class MarketTrendsController {
    private final CsvService csvService;

    public MarketTrendsController(CsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping("/profit")
    public List<StockData> getProfitData(@RequestParam String symbol,
                                         @RequestParam String range) throws Exception {
        List<StockData> marketItems = csvService.readFromCSV();
        marketItems = marketItems.stream()
                .filter(item -> item.getSymbol().equals(symbol))
                .collect(Collectors.toList());

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(getDaysFromRange(range));

        marketItems = marketItems.stream()
                .filter(item -> !item.getDate().isBefore(startDate) && !item.getDate().isAfter(endDate))
                .collect(Collectors.toList());

        marketItems.sort(Comparator.comparing(StockData::getDate));

        return marketItems;
    }

    private int getDaysFromRange(String range) {
        return switch (range) {
            case "1week" -> 7;
            case "1month" -> 30;
            case "1year" -> 365;
            case "5year" -> 1825;
            default -> 30;
        };
    }


}
