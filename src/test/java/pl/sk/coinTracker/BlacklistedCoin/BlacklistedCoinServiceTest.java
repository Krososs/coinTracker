package pl.sk.coinTracker.BlacklistedCoin;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BlacklistedCoinServiceTest {
    @Test
    void test_blacklisted_coin() {
        var mockBlacklistedCoinRepository = mock(BlacklistedCoinRepository.class);
        BlacklistedCoin coin = new BlacklistedCoin(1L, 1L);
        when(mockBlacklistedCoinRepository.save(coin)).thenReturn(coin);

        var testService = new BlacklistedCoinService(mockBlacklistedCoinRepository);
        BlacklistedCoin blacklistedCoin = testService.addCoinToBlackList(coin);

        assertEquals(coin.getCoinId(), blacklistedCoin.getCoinId());
        assertEquals(coin.getWalletId(), blacklistedCoin.getWalletId());
    }

    @Test
    void should_remove_coin_from_blacklist() {
        var blacklistedCoinRepository = inMemoryBlacklistedCoinRepository();

        BlacklistedCoin coin = new BlacklistedCoin(1L, 1L);
        BlacklistedCoin coin2 = new BlacklistedCoin(2L, 1L);

        var testService = new BlacklistedCoinService(blacklistedCoinRepository);
        blacklistedCoinRepository.save(coin);
        blacklistedCoinRepository.save(coin2);

        testService.removeCoinFromBlackList(1L, 1L);

        assertEquals(blacklistedCoinRepository.findByWalletId(1L).size(), 0);
        assertEquals(blacklistedCoinRepository.findByWalletId(2L).size(), 1);

    }

    @Test
    void should_return_all_blacklisted_coins() {
        var blacklistedCoinRepository = inMemoryBlacklistedCoinRepository();

        BlacklistedCoin coin = new BlacklistedCoin(1L, 1L);
        coin.setId(1L);
        BlacklistedCoin coin2 = new BlacklistedCoin(1L, 2L);
        coin2.setId(2L);
        BlacklistedCoin coin3 = new BlacklistedCoin(2L, 1L);
        coin3.setId(3L);

        var testService = new BlacklistedCoinService(blacklistedCoinRepository);
        blacklistedCoinRepository.save(coin);
        blacklistedCoinRepository.save(coin2);
        blacklistedCoinRepository.save(coin3);

        assertEquals(blacklistedCoinRepository.findByWalletId(1L).size(), 2);
        assertEquals(blacklistedCoinRepository.findByWalletId(2L).size(), 1);
    }

    @Test
    void test_if_coin_is_blacklisted_and_return_true() {
        var blacklistedCoinRepository = inMemoryBlacklistedCoinRepository();

        BlacklistedCoin coin = new BlacklistedCoin(1L, 1L);
        coin.setId(1L);
        BlacklistedCoin coin2 = new BlacklistedCoin(1L, 2L);
        coin2.setId(2L);

        var testService = new BlacklistedCoinService(blacklistedCoinRepository);
        blacklistedCoinRepository.save(coin);
        blacklistedCoinRepository.save(coin2);

        assertTrue(testService.coinIsBlacklisted(1L, 1L));
        assertTrue(testService.coinIsBlacklisted(1L, 2L));
    }

    @Test
    void test_if_coin_is_blacklisted_and_return_false() {
        var blacklistedCoinRepository = inMemoryBlacklistedCoinRepository();

        BlacklistedCoin coin = new BlacklistedCoin(1L, 1L);
        coin.setId(1L);
        BlacklistedCoin coin2 = new BlacklistedCoin(1L, 2L);
        coin2.setId(2L);

        var testService = new BlacklistedCoinService(blacklistedCoinRepository);
        blacklistedCoinRepository.save(coin);
        blacklistedCoinRepository.save(coin2);

        assertFalse(testService.coinIsBlacklisted(1L, 3L));
        assertFalse(testService.coinIsBlacklisted(2L, 2L));
    }


    private BlacklistedCoinRepository inMemoryBlacklistedCoinRepository() {
        Map<Long, BlacklistedCoin> blacklist = new HashMap<>();
        return new BlacklistedCoinRepository() {
            @Override
            public List<BlacklistedCoin> findByWalletId(Long id) {
                return blacklist.values().stream()
                        .filter(c -> c.getWalletId().equals(id))
                        .collect(Collectors.toList());
            }

            @Override
            public BlacklistedCoin save(BlacklistedCoin blacklistedCoin) {
                return blacklist.put(blacklistedCoin.getId(), blacklistedCoin);
            }

            @Override
            public void deleteByCoinId(Long walletId, Long coinId) {
                blacklist.entrySet().removeIf(entry -> entry.getValue().getWalletId().equals(walletId) && entry.getValue().getCoinId().equals(coinId));
            }
        };
    }
}