package mk.finki.ukim.mk.daswebapplication.service;

import mk.finki.ukim.mk.daswebapplication.model.StockData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicalIndicatorsService {
    public double calculateRSI(List<StockData> data, int period) {
        double gain = 0.0, loss = 0.0;

        for (int i = data.size() - period; i < data.size(); i++) {
            double change = data.get(i).getLastTransaction() - data.get(i - 1).getLastTransaction();
            if (change > 0) {
                gain += change;
            } else {
                loss -= change;
            }
        }

        double avgGain = gain / period;
        double avgLoss = loss / period;
        double rs = avgGain / avgLoss;

        return 100 - (100 / (1 + rs));
    }

    public double calculateSMA(List<StockData> stockData, int period) {
        if (stockData == null || stockData.size() < period) {
            throw new IllegalArgumentException("Insufficient data for SMA calculation");
        }

        double sum = 0.0;
        int startIndex = stockData.size() - period;

        for (int i = startIndex; i < stockData.size(); i++) {
            sum += stockData.get(i).getLastTransaction();
        }

        return sum / period;
    }


    public double calculateMACD(List<StockData> data) {
        double shortEma = calculateEMA(data, 12);
        double longEma = calculateEMA(data, 26);

        return shortEma - longEma;
    }


    private double calculateEMA(List<StockData> data, int period) {
        double multiplier = 2.0 / (period + 1);
        double ema = data.get(0).getLastTransaction();

        for (int i = 1; i < data.size(); i++) {
            double price = data.get(i).getLastTransaction();
            ema = (price - ema) * multiplier + ema;
        }

        return ema;
    }

    public double calculateStochastic(List<StockData> data, int period) {
        double highestHigh = Double.MIN_VALUE;
        double lowestLow = Double.MAX_VALUE;

        for (int i = data.size() - period; i < data.size(); i++) {
            if (data.get(i).getMax() > highestHigh) highestHigh = data.get(i).getMax();
            if (data.get(i).getMin() < lowestLow) lowestLow = data.get(i).getMin();
        }

        double currentClose = data.get(data.size() - 1).getLastTransaction();
        return (currentClose - lowestLow) / (highestHigh - lowestLow) * 100;
    }

    public String generateSignal(double rsi, double macd) {
        if (rsi > 70 && macd < 0) {
            return "Sell";
        } else if (rsi < 30 && macd > 0) {
            return "Buy";
        } else {
            return "Hold";
        }
    }
}
