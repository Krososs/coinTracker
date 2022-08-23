package pl.sk.coinTracker.CoinCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLCoinCategoryRepository extends CoinCategoryRepository, JpaRepository<CoinCategory,Long> {

}
