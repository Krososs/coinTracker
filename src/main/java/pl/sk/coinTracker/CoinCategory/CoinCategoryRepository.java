package pl.sk.coinTracker.CoinCategory;

import pl.sk.coinTracker.Transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface CoinCategoryRepository {
    List<CoinCategory> findByUserId(Long id);

    Optional<CoinCategory> findById(Long id);

    boolean existsById(Long id);

    CoinCategory save(CoinCategory coin);

    void deleteById(Long id);
}
