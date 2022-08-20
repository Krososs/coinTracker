package pl.sk.coinTracker.Coin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SQLCoinRepository extends CoinRepository, JpaRepository<Coin, Long> {
}
