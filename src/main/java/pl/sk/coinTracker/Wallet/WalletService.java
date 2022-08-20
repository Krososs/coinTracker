package pl.sk.coinTracker.Wallet;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createNewWallet(Wallet wallet, Long userId) {
        wallet.setOwnerId(userId);
        wallet.setAth(BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }

    public void editWallet(String name, Long walletId) {
        Wallet w = walletRepository.findById(walletId).get();
        w.setName(name);
        walletRepository.save(w);
    }

    public void deleteWallet(Long walletId) {
        walletRepository.deleteById(walletId);
    }

    public boolean walletNameAlreadyExists(String walletName, Long ownerId) {
        return walletRepository.findByOwnerId(ownerId).stream()
                .anyMatch(wallet -> wallet.getName().equals(walletName));
    }

    public boolean userIsOwner(Long userId, Long walletId) {
        Wallet w = walletRepository.findById(walletId).get();
        return w.getOwnerId().equals(userId);
    }

    public BigDecimal getPercentageChange(BigDecimal totalSpend, BigDecimal totalValue) {
        if ((totalSpend.equals(BigDecimal.ZERO) && totalValue.equals(BigDecimal.ZERO)) || (totalSpend.equals(BigDecimal.ZERO) && !totalValue.equals(BigDecimal.ZERO)))
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        double percentageChange = (totalValue.doubleValue() - totalSpend.doubleValue()) / totalSpend.doubleValue() * 100.0;
        return new BigDecimal(percentageChange).setScale(2, RoundingMode.HALF_EVEN);
    }

    public boolean walletExists(Long id) {
        return walletRepository.findById(id).isPresent();
    }

    public List<Wallet> getUserWallets(Long userId) {
        return walletRepository.findByOwnerId(userId);
    }

    public Wallet getWalletById(Long id) {
        return walletRepository.findById(id).get();
    }
}
