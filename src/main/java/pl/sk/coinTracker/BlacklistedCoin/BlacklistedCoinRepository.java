package pl.sk.coinTracker.BlacklistedCoin;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlacklistedCoinRepository {
    List<BlacklistedCoin> findByWalletId(Long id);

    BlacklistedCoin save (BlacklistedCoin blacklistedCoin);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM BlacklistedCoin c WHERE wallet_id = :walletId AND c.coinId = :coinId")
    void deleteByCoinId(@Param("walletId") Long walletId, @Param("coinId") Long coinId);
}
