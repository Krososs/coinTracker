package pl.sk.coinTracker.Coin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.constant.Currency;
import org.springframework.stereotype.Service;
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

        if ((long) coinRepository.findByTicker(ticker).size() > 2)
            return false;
        return coinRepository.findByTicker(ticker).stream().findFirst().isPresent();
    }

    public List<Coin> getAll() {
        return coinRepository.findAll();
    }

    public Coin getCoinById(Long id) {
        return coinRepository.getById(id);
    }

    public Coin getCoinByTicker(String ticker) {

        if ((long) coinRepository.findByTicker(ticker).size() == 2) {
            List<Coin> coins = coinRepository.findByTicker(ticker);

            if ((coins.stream().findFirst().get().getCoinrank() < coins.get(coins.size() - 1).getCoinrank()) && coins.stream().findFirst().get().getCoinrank() != 0)
                return coins.stream().findFirst().get();
            else if ((coins.get(coins.size() - 1).getCoinrank() < coins.stream().findFirst().get().getCoinrank()) && coins.get(coins.size() - 1).getCoinrank() != 0)
                return coins.get(coins.size() - 1);
        }
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
        return coinRepository.getById(id).getIdentifier();
    }

    public Double getPrice(Long id) {

        String coinIdentifier = getCoinIdentifierFromId(id);
        Map<String, Map<String, Double>> priceData;
        Map.Entry<String, Double> entry;

        try {
            priceData = client.getPrice(coinIdentifier, Currency.USD);
            Map<String, Double> data = priceData.entrySet().iterator().next().getValue();
            entry = data.entrySet().iterator().next();

        } catch (Exception e) {
            return 0.0;
        }
        return entry.getValue();
    }

    public ObjectNode getCoinInfo(Long id, Double amount, Double price, Double value, String date) {

        ObjectNode coinInfo = new ObjectMapper().createObjectNode();
        coinInfo.put("info", getCoinById(id).toJson());
        coinInfo.put("amount", (Math.round(amount * 100.0) / 100.0));
        coinInfo.put("price", Math.round(price * 10000.0) / 10000.0);
        coinInfo.put("value", Math.round(value * 100.0) / 100.0);
        coinInfo.put("addingDate", date);

        return coinInfo;
    }
}
