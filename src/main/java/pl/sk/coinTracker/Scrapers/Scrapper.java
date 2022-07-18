package pl.sk.coinTracker.Scrapers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class Scrapper {
    public abstract List<Map<String, String>> getAccountTokens(String accountAddress) throws IOException;

    public abstract Map<String, Double> getNativeCurrencyBalance(String accountAddress) throws IOException;

    public abstract String getNativeCurrencyTicker();

    public String removeLetters(String expression) {
        StringBuffer buffer = new StringBuffer(expression);

        for (int i = 0; i < expression.length(); i++) {
            if (Character.isLetter(expression.charAt(i)))
                buffer.replace(i, i + 1, " ");
        }
        return buffer.toString();
    }

    public static Scrapper get (String chain){
        switch (chain){
            case "ETHEREUM":
                return new EtherscanScraper();
            case "BINANCE_SMART_CHAIN":
                return new BSCScanScraper();
            default:
                return new EtherscanScraper();
        }
    }
}
