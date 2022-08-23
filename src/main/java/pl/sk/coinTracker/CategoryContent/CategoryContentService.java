package pl.sk.coinTracker.CategoryContent;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryContentService {
    private final CategoryContentRepository contentRepository;

    public CategoryContentService(CategoryContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public CategoryContent categorize(CategoryContent content) {
        return contentRepository.save(content);
    }

    public void decategorize(Long contentId) {
        contentRepository.deleteById(contentId);
    }

    public boolean categorized(Long categoryId, Long coinId) {
        return contentRepository.findByCategoryId(categoryId)
                .stream()
                .anyMatch(content -> content.getCoinId().equals(coinId));
    }

    public boolean contentExists(Long contentId) {
        return contentRepository.existsById(contentId);
    }

    public CategoryContent getById(Long id) {
        return contentRepository.findById(id).get();
    }

    public List<CategoryContent> getByCategoryId(Long categoryId) {
        return contentRepository.findByCategoryId(categoryId);
    }
}
