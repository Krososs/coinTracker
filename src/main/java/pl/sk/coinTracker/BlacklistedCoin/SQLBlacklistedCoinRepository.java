package pl.sk.coinTracker.BlacklistedCoin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SQLBlacklistedCoinRepository extends BlacklistedCoinRepository, JpaRepository<BlacklistedCoin, Long> {
}
