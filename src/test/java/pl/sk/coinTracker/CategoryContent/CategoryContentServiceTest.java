package pl.sk.coinTracker.CategoryContent;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryContentServiceTest {

    @Test
    void test_categorize_coin() {
        var mockContentRepository = mock(CategoryContentRepository.class);
        CategoryContent content = new CategoryContent(1L, 2L);
        when(mockContentRepository.save(content)).thenReturn(content);

        var testService = new CategoryContentService(mockContentRepository);
        CategoryContent createdContent = testService.categorize(content);

        assertEquals(content.getCategoryId(), createdContent.getCategoryId());
        assertEquals(content.getCoinId(), createdContent.getCoinId());
    }

    @Test
    void test_decategorize_coin() {
        var contentRepository = inMemoryCategoryContentRepository();
        CategoryContent content = new CategoryContent(1L, 2L);
        content.setId(1L);

        var testService = new CategoryContentService(contentRepository);
        contentRepository.save(content);
        testService.decategorize(1L);

        assertFalse(contentRepository.existsById(1L));
    }

    @Test
    void check_if_coin_is_categorized_and_return_true() {
        var contentRepository = inMemoryCategoryContentRepository();
        CategoryContent content = new CategoryContent(1L, 2L);
        content.setId(1L);

        var testService = new CategoryContentService(contentRepository);
        contentRepository.save(content);

        assertTrue(testService.categorized(1L, 2L));
    }

    @Test
    void check_if_coin_is_categorized_and_return_false() {
        var contentRepository = inMemoryCategoryContentRepository();
        CategoryContent content = new CategoryContent(1L, 2L);
        content.setId(1L);

        var testService = new CategoryContentService(contentRepository);
        contentRepository.save(content);

        assertFalse(testService.categorized(2L, 1L));
        assertFalse(testService.categorized(2L, 2L));
    }

    @Test
    void test_if_content_exist_and_return_true() {
        var contentRepository = inMemoryCategoryContentRepository();
        CategoryContent content = new CategoryContent(1L, 2L);
        content.setId(1L);

        var testService = new CategoryContentService(contentRepository);
        contentRepository.save(content);

        assertTrue(testService.contentExists(1L));
    }

    @Test
    void test_if_content_exist_and_return_false() {
        var contentRepository = inMemoryCategoryContentRepository();
        CategoryContent content = new CategoryContent(1L, 2L);
        content.setId(1L);

        var testService = new CategoryContentService(contentRepository);
        contentRepository.save(content);

        assertFalse(testService.contentExists(2L));
    }

    @Test
    void should_return_content_by_id() {
        var contentRepository = inMemoryCategoryContentRepository();
        CategoryContent content = new CategoryContent(1L, 2L);
        content.setId(1L);

        var testService = new CategoryContentService(contentRepository);
        contentRepository.save(content);

        assertEquals(2L, testService.getById(1L).getCoinId());
        assertEquals(1L, testService.getById(1L).getCategoryId());
    }

    @Test
    void should_return_content_by_category_id() {
        var contentRepository = inMemoryCategoryContentRepository();

        CategoryContent content = new CategoryContent(1L, 2L);
        content.setId(1L);

        CategoryContent content2 = new CategoryContent(1L, 3L);
        content2.setId(2L);

        var testService = new CategoryContentService(contentRepository);
        contentRepository.save(content);
        contentRepository.save(content2);

        assertEquals(2, testService.getByCategoryId(1L).size());
    }

    private CategoryContentRepository inMemoryCategoryContentRepository() {
        Map<Long, CategoryContent> contentList = new HashMap<>();
        return new CategoryContentRepository() {
            @Override
            public List<CategoryContent> findByCategoryId(Long id) {
                return contentList.values().stream()
                        .filter(content -> content.getCategoryId().equals(id))
                        .collect(Collectors.toList());
            }

            @Override
            public Optional<CategoryContent> findById(Long id) {
                return Optional.ofNullable(contentList.get(id));
            }

            @Override
            public boolean existsById(Long id) {
                return contentList.values().stream()
                        .anyMatch(content -> content.getId().equals(id));
            }

            @Override
            public CategoryContent save(CategoryContent content) {
                return contentList.put(content.getId(), content);
            }

            @Override
            public void deleteById(Long id) {
                contentList.remove(id);
            }
        };
    }
}