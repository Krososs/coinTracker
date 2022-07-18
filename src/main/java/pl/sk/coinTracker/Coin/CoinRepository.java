package pl.sk.coinTracker.Coin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {

    Optional<Coin> findByName(String name);
    List<Coin> findByNameContaining(String name);

    List<Coin> findByTicker(String ticker);
    List<Coin>findByTickerContaining(String ticker);

    Optional<Coin>findByIdentifier(String identifier);
    Optional<Coin>findByIdentifierContaining(String identifier);
}
