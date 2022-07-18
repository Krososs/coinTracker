package pl.sk.coinTracker.Scrapers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BSCScanScraper extends Scrapper {

    @Override
    public List<Map<String, String>> getAccountTokens(String address) throws IOException {
        return null;
    }

    @Override
    public Map<String, Double> getNativeCurrencyBalance(String accountAddress) throws IOException {
        return null;
    }

    @Override
    public String getNativeCurrencyTicker() {
        return null;
    }
}
