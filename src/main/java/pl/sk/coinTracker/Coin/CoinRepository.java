package pl.sk.coinTracker.Coin;

import pl.sk.coinTracker.User.User;

import java.util.List;
import java.util.Optional;

public interface CoinRepository {
    Optional<Coin> findByName(String name);
    Optional<Coin> findById(Long id);
    List<Coin> findByNameContaining(String name);

    List<Coin> findAll();
    List<Coin> findByTicker(String ticker);
    List<Coin>findByTickerContaining(String ticker);


    Optional<Coin>findByIdentifier(String identifier);
    Optional<Coin>findByIdentifierContaining(String identifier);

    boolean existsById(Long id);

    Coin save(Coin coin);
}
