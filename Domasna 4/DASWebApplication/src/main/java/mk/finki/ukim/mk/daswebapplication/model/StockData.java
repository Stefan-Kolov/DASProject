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
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(bestProfit);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(double lastTransaction) {
        this.lastTransaction = lastTransaction;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getBestProfit() {
        return bestProfit;
    }

    public void setBestProfit(double bestProfit) {
        this.bestProfit = bestProfit;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }
}