package mk.finki.ukim.mk.daswebapplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.DecimalFormat;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class StockData {

    private String symbol;
    private LocalDate date;
    private Double lastTransaction;
    private Double min;
    private Double max;
    private Double avg;
    private Double percent;
    private Integer quantity;
    private Double bestProfit;
    private Double totalProfit;

    public StockData() {

    }

    public String getTotalProfitFormatted() {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(bestProfit);
    }
}