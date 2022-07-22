package pl.sk.coinTracker.CoinCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinCategoryRepository extends JpaRepository<CoinCategory,Long> {
    List<CoinCategory> findByUserId(Long id);
}
