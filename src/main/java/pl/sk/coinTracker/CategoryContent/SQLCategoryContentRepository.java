package pl.sk.coinTracker.CategoryContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLCategoryContentRepository extends CategoryContentRepository, JpaRepository<CategoryContent, Long> {

}
