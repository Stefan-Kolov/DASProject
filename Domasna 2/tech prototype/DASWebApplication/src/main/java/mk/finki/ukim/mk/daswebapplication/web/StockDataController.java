package mk.finki.ukim.mk.daswebapplication.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.service.annotation.GetExchange;

@Controller
@RequestMapping("/stock-data")
public class StockDataController {

    @GetMapping
    public String getStockData() {
        return "stock-data";
    }
}
