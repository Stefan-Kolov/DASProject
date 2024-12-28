package mk.finki.ukim.mk.daswebapplication.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import mk.finki.ukim.mk.daswebapplication.model.StockData;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CsvService {
    private static final String CSV_FILE_PATH = "src/main/resources/static/stock_data.csv";

    public List<StockData> readCSV() throws IOException {
        List<StockData> marketItems = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.M.yyyy");
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            reader.readNext();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                StockData item = new StockData();
                item.setSymbol(nextLine[0]);
                item.setDate(LocalDate.parse(nextLine[1],formatter));
                item.setLastTransaction(parseDouble(nextLine[2]));
                item.setMax(parseDouble(nextLine[3]));
                item.setMin(parseDouble(nextLine[4]));
                item.setAvg(parseDouble(nextLine[5]));
                item.setPercent(parseDouble(nextLine[6]));
                item.setQuantity((int)parseDouble(nextLine[7]));
                item.setBestProfit(parseDouble(nextLine[8]));
                item.setTotalProfit(parseDouble(nextLine[9]));

                System.out.println("Symbol: " + item.getSymbol() + ", Date: " + item.getDate() + ", Total Profit: " + item.getTotalProfit());

                marketItems.add(item);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return marketItems;
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }

        String cleanValue = value.replace(".", "");
        cleanValue = cleanValue.replace(",", ".");

        try {
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + value);
            return 0.0;
        }
    }


    public List<String> getAvailableSymbols() throws IOException {
        List<StockData> marketItems = readCSV();

        return marketItems.stream()
                .map(StockData::getSymbol)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<StockData> getTop10SymbolsByLatestDateAndProfit() throws IOException {
        List<StockData> marketItems = readCSV();

        Optional<LocalDate> latestDate = marketItems.stream()
                .map(StockData::getDate)
                .max(LocalDate::compareTo);

        if (latestDate.isEmpty()) {
            return Collections.emptyList();
        }

        List<StockData> latestData = marketItems.stream()
                .filter(item -> item.getDate().equals(latestDate.get()))
                .toList();

        return latestData.stream()
                .sorted((item1, item2) -> Double.compare(item2.getTotalProfit(), item1.getTotalProfit()))
                .limit(10)
                .collect(Collectors.toList());
    }
}