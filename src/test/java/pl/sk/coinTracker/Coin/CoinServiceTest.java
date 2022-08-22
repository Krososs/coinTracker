package pl.sk.coinTracker.Coin;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CoinServiceTest {

    @Test
    void check_if_coin_exist_by_ticker_and_return_true() {
        var coinRepository = inMemoryCoinRepository();
        Coin coin1 = new Coin();
        coin1.setTicker("ticker");

        var testService = new CoinService(coinRepository, null);
        coinRepository.save(coin1);

        assertTrue(testService.coinExistsByTicker("ticker"));
    }

    @Test
    void check_if_coin_exist_by_ticker_and_return_false() {
        var coinRepository = inMemoryCoinRepository();
        Coin coin1 = new Coin();
        coin1.setId(1L);
        coin1.setTicker("ticker");
        Coin coin2 = new Coin();
        coin2.setId(2L);
        coin2.setTicker("ticker");
        Coin coin3 = new Coin();
        coin3.setId(3L);
        coin3.setTicker("ticker");

        var testService = new CoinService(coinRepository, null);
        coinRepository.save(coin1);
        coinRepository.save(coin2);
        coinRepository.save(coin3);

        assertFalse(testService.coinExistsByTicker("ticker"));
    }

    @Test
    void get_coin_by_ticker_and_return_coin_with_lower_rank() {
        var coinRepository = inMemoryCoinRepository();
        Coin coin1 = new Coin();
        coin1.setId(1L);
        coin1.setCoinrank(1L);
        coin1.setTicker("ticker");
        Coin coin2 = new Coin();
        coin2.setId(2L);
        coin2.setCoinrank(2L);
        coin2.setTicker("ticker");

        var testService = new CoinService(coinRepository, null);
        coinRepository.save(coin1);
        coinRepository.save(coin2);

        Coin givenCoin = testService.getCoinByTicker("ticker");

        assertEquals(givenCoin.getCoinrank(), 1L);
        assertEquals(givenCoin.getId(), 1L);

    }

    @Test
    void should_return_coin_with_given_ticker() {

        var coinRepository = inMemoryCoinRepository();
        Coin coin1 = new Coin();
        coin1.setId(1L);
        coin1.setCoinrank(1L);
        coin1.setTicker("ticker");
        Coin coin2 = new Coin();
        coin2.setId(2L);
        coin2.setCoinrank(2L);
        coin2.setTicker("ticker2");

        var testService = new CoinService(coinRepository, null);
        coinRepository.save(coin1);
        coinRepository.save(coin2);

        Coin givenCoin = testService.getCoinByTicker("ticker");

        assertEquals(givenCoin.getId(), 1L);
        assertEquals(givenCoin.getCoinrank(), 1L);
    }

    @Test
    void should_return_coins_with_name_containing_phrase() {
        var coinRepository = inMemoryCoinRepository();
        Coin coin1 = new Coin();
        coin1.setId(1L);
        coin1.setTicker("t1");
        coin1.setName("anameCoin");

        Coin coin2 = new Coin();
        coin2.setId(2L);
        coin2.setTicker("t2");
        coin2.setName("bnameCoin");

        var testService = new CoinService(coinRepository, null);
        coinRepository.save(coin1);
        coinRepository.save(coin2);

        List<Coin> givenCoins = testService.getCoinsByPhrase("name");

        assertEquals(givenCoins.size(), 2);
    }

    @Test
    void should_return_coins_with_ticker_containing_phrase() {
        var coinRepository = inMemoryCoinRepository();
        Coin coin1 = new Coin();
        coin1.setId(1L);
        coin1.setTicker("aTickerCoin");
        coin1.setName("name1");

        Coin coin2 = new Coin();
        coin2.setId(2L);
        coin2.setTicker("bTickerCoin");
        coin2.setName("name2");

        var testService = new CoinService(coinRepository, null);
        coinRepository.save(coin1);
        coinRepository.save(coin2);

        List<Coin> givenCoins = testService.getCoinsByPhrase("Ticker");

        assertEquals(givenCoins.size(), 2);
    }

    @Test
    void should_return_coin_price() {
        var coinRepository = inMemoryCoinRepository();
        Coin coin1 = new Coin();
        coin1.setId(1L);
        coin1.setIdentifier("usd-coin");

        var testService = new CoinService(coinRepository, new CoinGeckoApiClientImpl());
        coinRepository.save(coin1);

        BigDecimal price = testService.getPrice(1L);
        assertTrue(price.compareTo(new BigDecimal(0.95)) >= 0 && price.compareTo(new BigDecimal(1.05)) <= 0);
    }

    @Test
    void should_return_coin_info() {
        var coinRepository = inMemoryCoinRepository();
        Coin coin1 = new Coin();
        coin1.setId(1L);
        coin1.setTicker("usdc");
        coin1.setName("usd-coin");
        coin1.setImage("image");
        coin1.setCoinrank(4L);
        coin1.setIdentifier("usd-coin");

        coinRepository.save(coin1);

        var testService = new CoinService(coinRepository, null);
        ObjectNode coinInfo = testService.getCoinInfo(1L, new BigDecimal(11.11), new BigDecimal(1.00), new BigDecimal(11.11), "date");

        assertEquals(coinInfo.get("info").get("name").asText(), "usd-coin");
        assertEquals(coinInfo.get("info").get("ticker").asText(), "usdc");
        assertEquals(coinInfo.get("info").get("rank").asLong(), 4L);
        assertEquals(coinInfo.get("info").get("image").asText(), "image");
        assertEquals(coinInfo.get("info").get("id").asLong(), 1L);
    }

    @Test
    void _test_(){
        Map<String, String> coin = new HashMap<>();
        coin.put("balance", "1,431,271.35672549");

        BigDecimal t = new BigDecimal(coin.get("balance").replaceAll(",",""));
    }

    private CoinRepository inMemoryCoinRepository() {
        Map<Long, Coin> coins = new HashMap<>();
        return new CoinRepository() {
            @Override
            public Optional<Coin> findByName(String name) {
                return Optional.empty();
            }

            @Override
            public Optional<Coin> findById(Long id) {
                Optional<Coin> result = coins.values().stream()
                        .filter(coin -> coin.getId().equals(id))
                        .findFirst();
                return result.isPresent() ? result : Optional.empty();
            }

            @Override
            public List<Coin> findByNameContaining(String name) {
                return coins.values().stream()
                        .filter(coin -> coin.getName().contains(name))
                        .collect(Collectors.toList());
            }

            @Override
            public List<Coin> findAll() {
                return null;
            }

            @Override
            public List<Coin> findByTicker(String ticker) {
                return coins.values().stream()
                        .filter(coin -> coin.getTicker().equals(ticker))
                        .collect(Collectors.toList());
            }

            @Override
            public List<Coin> findByTickerContaining(String ticker) {
                return coins.values().stream()
                        .filter(coin -> coin.getTicker().contains(ticker))
                        .collect(Collectors.toList());
            }

            @Override
            public Optional<Coin> findByIdentifier(String identifier) {
                return Optional.empty();
            }

            @Override
            public Optional<Coin> findByIdentifierContaining(String identifier) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long id) {
                return false;
            }

            @Override
            public Coin save(Coin coin) {
                return coins.put(coin.getId(), coin);
            }
        };

    }
}