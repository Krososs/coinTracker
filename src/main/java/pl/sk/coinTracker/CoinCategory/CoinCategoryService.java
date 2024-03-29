package pl.sk.coinTracker.CoinCategory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinCategoryService {
    private final CoinCategoryRepository categoryRepository;

    public CoinCategoryService(CoinCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CoinCategory createNewCategory(CoinCategory category) {
        return categoryRepository.save(category);
    }

    public CoinCategory getCategoryById(Long id){
        return  categoryRepository.findById(id).get();
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public boolean categoryExists(Long userId, String name) {
        for (CoinCategory c : categoryRepository.findByUserId(userId)) {
            if (c.getName().equals(name))
                return true;
        }
        return false;
    }

    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }

    public boolean userIsOwner(Long userId, Long categoryId) {
        CoinCategory c = categoryRepository.findById(categoryId).get();
        return c.getUserId().equals(userId);
    }

    public List<CoinCategory> getUserCategories(Long userId) {
        return categoryRepository.findByUserId(userId);
    }
}
