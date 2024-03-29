package pl.sk.coinTracker.Coin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.constant.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class CoinService {

    private final CoinRepository coinRepository;
    private final CoinGeckoApiClient client;

    public CoinService(CoinRepository coinRepository, CoinGeckoApiClient client) {
        this.coinRepository = coinRepository;
        this.client = client;
    }

    public boolean coinExistsById(Long id) {
        return coinRepository.existsById(id);
    }

    public boolean coinExistsByTicker(String ticker) {
        return coinRepository.findByTicker(ticker).size() > 2 ?
                false : coinRepository.findByTicker(ticker).stream().findFirst().isPresent();
    }

    public List<Coin> getAll() {
        return coinRepository.findAll();
    }

    public Coin getCoinById(Long id) {
        return coinRepository.findById(id).get();
    }

    public Coin getCoinByTicker(String ticker) {
        if (coinRepository.findByTicker(ticker).size() > 1)
            return coinRepository.findByTicker(ticker).stream()
                    .sorted(Coin::compareByCoinRank)
                    .findFirst().get();
        return coinRepository.findByTicker(ticker).stream().findFirst().get();
    }

    public List<Coin> getCoinsByPhrase(String phrase) {

        List<Coin> coins = coinRepository.findByNameContaining(phrase);
        List<Coin> coins2 = coinRepository.findByTickerContaining(phrase);

        for (Coin c : coins2) {
            if (!coins.contains(c))
                coins.add(c);
        }
        return coins;
    }

    public String getCoinIdentifierFromId(Long id) {
        return coinRepository.findById(id).get().getIdentifier();
    }

    public BigDecimal getPrice(Long id) {

        String coinIdentifier = getCoinIdentifierFromId(id);
        Map<String, Map<String, Double>> priceData;
        Map.Entry<String, Double> entry;

        try {
            priceData = client.getPrice(coinIdentifier, Currency.USD);
            Map<String, Double> data = priceData.entrySet().iterator().next().getValue();
            entry = data.entrySet().iterator().next();

        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(entry.getValue());
    }

    public ObjectNode getCoinInfo(Long id, BigDecimal amount, BigDecimal price, BigDecimal value, String date) {

        ObjectNode coinInfo = new ObjectMapper().createObjectNode();
        coinInfo.put("info", getCoinById(id).toJson());
        coinInfo.put("amount", amount.setScale(2, RoundingMode.HALF_EVEN));
        coinInfo.put("price", price.setScale(2, RoundingMode.HALF_EVEN));
        coinInfo.put("value", value.setScale(2, RoundingMode.HALF_EVEN));
        coinInfo.put("addingDate", date);
        return coinInfo;
    }
}
