package pl.sk.coinTracker.Wallet;

import org.springframework.stereotype.Service;
import pl.sk.coinTracker.User.User;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void createNewWallet(Wallet wallet, User owner) {
        wallet.setOwnerId(owner.getId());
        wallet.setAth(BigDecimal.ZERO);
        walletRepository.save(wallet);
    }

    public void editWallet(Wallet wallet, Long walletId){
        Wallet w = walletRepository.findById(walletId).get();
        w.setName(wallet.getName());
        walletRepository.save(w);
    }

    public void deleteWallet(Long walletId){
        walletRepository.deleteById(walletId);
    }

    public boolean walletNameAlreadyExists(String walletName, Long ownerId) {
        for (Wallet w : walletRepository.findByOwnerId(ownerId)) {
            if (w.getName().equals(walletName))
                return true;
        }
        return false;
    }

    public boolean userIsOwner(Long userId, Long walletId) {
        Wallet w = walletRepository.findById(walletId).get();
        return w.getOwnerId().equals(userId);
    }

    public String getWalletName(Long walletId) {
        return walletRepository.findById(walletId).get().getName();
    }

    public BigDecimal getPercentageChange(BigDecimal totalSpend, BigDecimal totalValue) {
        if(totalSpend.equals(BigDecimal.ZERO) || totalValue.equals(BigDecimal.ZERO))
            return BigDecimal.ZERO;
        return totalValue.subtract(totalSpend).divide(totalSpend).multiply(BigDecimal.valueOf(100));
    }

    public boolean walletExists(Long id) {
        return walletRepository.findById(id).isPresent();
    }

    public List<Wallet> getUserWallets(Long userId) {
        return walletRepository.findByOwnerId(userId);
    }
    public Wallet getWalletById(Long id){ return  walletRepository.findById(id).get();}
}
