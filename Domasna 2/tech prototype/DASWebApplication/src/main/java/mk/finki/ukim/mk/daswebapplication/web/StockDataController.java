package mk.finki.ukim.mk.daswebapplication.web;

import mk.finki.ukim.mk.daswebapplication.model.StockData;
import mk.finki.ukim.mk.daswebapplication.service.CsvService;
import mk.finki.ukim.mk.daswebapplication.service.TechnicalIndicatorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/stock-data")
public class StockDataController {

    private final TechnicalIndicatorsService technicalIndicatorsService;
    private final CsvService csvService;

    @Autowired
    public StockDataController(TechnicalIndicatorsService technicalIndicatorsService, CsvService csvService) {
        this.technicalIndicatorsService = technicalIndicatorsService;
        this.csvService = csvService;
    }

    @GetMapping
    public String showStockDataPage(Model model) throws IOException {
        List<String> symbols = csvService.getAvailableSymbols();
        model.addAttribute("symbols", symbols);
        return "stock-data";
    }

    @GetMapping("/analyze")
    @ResponseBody
    public Map<String, Object> analyzeStock(
            @RequestParam("symbol") String symbol,
            @RequestParam("range") String range) throws IOException {

        List<StockData> stockData = csvService.readCSV();
        System.out.println("Received symbol: " + symbol + ", range: " + range);

        stockData = filterStockDataBySymbol(stockData, symbol);
        stockData = filterStockDataByRange(stockData, range);
        stockData.sort(Comparator.comparing(StockData::getDate));

        System.out.println("Filtered stock data size: " + stockData.size());

        double rsi = technicalIndicatorsService.calculateRSI(stockData, 14);
        double macd = technicalIndicatorsService.calculateMACD(stockData);
        double stochastic = technicalIndicatorsService.calculateStochastic(stockData, 14);
        String signal = technicalIndicatorsService.generateSignal(rsi, macd);

        Map<String, Object> response = new HashMap<>();
        response.put("rsi", rsi);
        response.put("macd", macd);
        response.put("stochastic", stochastic);
        response.put("signal", signal);
        response.put("stockData", stockData);

        return response;
    }


    private List<StockData> filterStockDataBySymbol(List<StockData> stockData, String symbol) {
        List<StockData> filteredData = new ArrayList<>();
        for (StockData data : stockData) {
            if (data.getSymbol().equals(symbol)) {
                filteredData.add(data);
            }
        }
        return filteredData;
    }

    private List<StockData> filterStockDataByRange(List<StockData> stockData, String range) {
        List<StockData> filteredData = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (StockData data : stockData) {
            LocalDate date = data.getDate();
            System.out.println("Checking stock data with date: " + date + " and current date: " + now);

            switch (range) {
                case "1week":
                    if (!date.isBefore(now.minusWeeks(1))) {
                        filteredData.add(data);
                    }
                    break;
                case "1month":
                    if (!date.isBefore(now.minusMonths(1))) {
                        filteredData.add(data);
                    }
                    break;
                case "1year":
                    if (!date.isBefore(now.minusYears(1))) {
                        filteredData.add(data);
                    }
                    break;
                case "5year":
                    if (!date.isBefore(now.minusYears(5))) {
                        filteredData.add(data);
                    }
                    break;
                default:
                    filteredData.add(data);
            }
        }

        System.out.println("Filtered data size: " + filteredData.size());
        return filteredData;
    }

}
