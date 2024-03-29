package pl.sk.coinTracker.Transaction;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.sk.coinTracker.Wallet.Wallet;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    @Transactional
    @Query(value = "SELECT t FROM Transaction t WHERE wallet_id = :walletId AND t.coinId = :coinId AND t.amount = 0 AND t.price = 0")
    Optional<Transaction> getInitialTransaction(@Param("walletId") Long walletId, @Param("coinId") Long coinId);

    @Transactional
    @Query(value = "SELECT t FROM Transaction t WHERE wallet_id = :walletId AND t.coinId = :coinId")
    List<Transaction> getTransactions(@Param("walletId") Long walletId, @Param("coinId") Long coinId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Transaction t WHERE wallet_id = :walletId AND t.coinId = :coinId")
    void deleteByCoinId(@Param("walletId") Long walletId, @Param("coinId") Long coinId);

    List<Transaction> findByWallet(Wallet w);

    Optional<Transaction> findById(Long id);

    boolean existsById(Long id);

    Transaction save(Transaction coin);

    void deleteById(Long id);
}
