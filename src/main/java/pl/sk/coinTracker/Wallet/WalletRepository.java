package pl.sk.coinTracker.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepository {
    List<Wallet> findByOwnerId(Long id);

    Optional<Wallet> findById(Long id);

    Wallet save(Wallet wallet);

    void deleteById(Long id);
}
