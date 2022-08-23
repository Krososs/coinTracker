package pl.sk.coinTracker.BlacklistedCoin;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlacklistedCoinService {

    private final BlacklistedCoinRepository blacklistedCoinRepository;

    public BlacklistedCoinService(BlacklistedCoinRepository blacklistedCoinRepository) {
        this.blacklistedCoinRepository = blacklistedCoinRepository;
    }

    public BlacklistedCoin addCoinToBlackList(BlacklistedCoin blacklistedCoin) {
        return blacklistedCoinRepository.save(blacklistedCoin);
    }

    public void removeCoinFromBlackList(Long walletId, Long coinId) {
        blacklistedCoinRepository.deleteByCoinId(walletId, coinId);
    }

    public List<BlacklistedCoin> getBlacklistedCoins(Long walletId) {
        return blacklistedCoinRepository.findByWalletId(walletId);
    }

    public boolean coinIsBlacklisted(Long walletId, Long coinId) {
        return blacklistedCoinRepository.findByWalletId(walletId).stream()
                .anyMatch(blacklistedCoin -> blacklistedCoin.getCoinId().equals(coinId));
    }
}
