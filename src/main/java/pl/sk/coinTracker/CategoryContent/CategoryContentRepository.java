package pl.sk.coinTracker.CategoryContent;

import pl.sk.coinTracker.Transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface CategoryContentRepository {
    List<CategoryContent> findByCategoryId(Long id);

    Optional<CategoryContent> findById(Long id);

    boolean existsById(Long id);

    CategoryContent save(CategoryContent content);

    void deleteById(Long id);
}
