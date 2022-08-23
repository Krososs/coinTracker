package pl.sk.coinTracker.CoinCategory;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CoinCategoryServiceTest {
    @Test
    void test_create_new_category() {
        var mockCoinCategoryRepository = mock(CoinCategoryRepository.class);
        CoinCategory c = new CoinCategory();
        c.setId(1L);
        c.setUserId(1L);
        c.setName("category");
        when(mockCoinCategoryRepository.save(c)).thenReturn(c);

        var testService = new CoinCategoryService(mockCoinCategoryRepository);
        CoinCategory createdCategory = testService.createNewCategory(c);

        assertEquals(c.getId(), createdCategory.getId());
        assertEquals(c.getUserId(), createdCategory.getUserId());
        assertEquals(c.getName(), createdCategory.getName());
    }

    @Test
    void test_delete_category() {
        var coinCategoryRepository = inMemoryCoinCategoryRepository();
        CoinCategory c = new CoinCategory();
        c.setId(1L);

        var testService = new CoinCategoryService(coinCategoryRepository);
        coinCategoryRepository.save(c);
        testService.deleteCategory(1L);

        assertFalse(coinCategoryRepository.existsById(1L));
    }

    @Test
    void check_if_category_exist_by_name_and_return_true() {
        var coinCategoryRepository = inMemoryCoinCategoryRepository();
        CoinCategory c = new CoinCategory();
        c.setId(1L);
        c.setUserId(1L);
        c.setName("name");

        var testService = new CoinCategoryService(coinCategoryRepository);
        coinCategoryRepository.save(c);

        assertTrue(testService.categoryExists(1L,"name"));
    }

    @Test
    void check_if_category_exist_by_name_and_return_false() {
        var coinCategoryRepository = inMemoryCoinCategoryRepository();
        CoinCategory c = new CoinCategory();
        c.setId(1L);
        c.setUserId(1L);
        c.setName("name");

        var testService = new CoinCategoryService(coinCategoryRepository);
        coinCategoryRepository.save(c);

        assertFalse(testService.categoryExists(2L,"name"));
        assertFalse(testService.categoryExists(1L,"name2"));
    }

    @Test
    void check_if_category_exist_by_id_and_return_true() {
        var coinCategoryRepository = inMemoryCoinCategoryRepository();
        CoinCategory c = new CoinCategory();
        c.setId(1L);

        var testService = new CoinCategoryService(coinCategoryRepository);
        coinCategoryRepository.save(c);

        assertTrue(testService.categoryExists(1L));
    }

    @Test
    void check_if_category_exist_by_id_and_return_false() {
        var coinCategoryRepository = inMemoryCoinCategoryRepository();
        var testService = new CoinCategoryService(coinCategoryRepository);
        assertFalse(testService.categoryExists(1L));
    }

    @Test
    void check_if_user_is_owner_and_return_true(){
        var coinCategoryRepository = inMemoryCoinCategoryRepository();

        CoinCategory c = new CoinCategory();
        c.setId(1L);
        c.setUserId(1L);

        CoinCategory c1 = new CoinCategory();
        c1.setId(2L);
        c1.setUserId(1L);

        var testService = new CoinCategoryService(coinCategoryRepository);
        coinCategoryRepository.save(c);
        coinCategoryRepository.save(c1);

        assertTrue(testService.userIsOwner(1L,1L));
        assertTrue(testService.userIsOwner(1L,2L));
    }

    @Test
    void check_if_user_is_owner_and_return_false(){
        var coinCategoryRepository = inMemoryCoinCategoryRepository();

        CoinCategory c = new CoinCategory();
        c.setId(1L);
        c.setUserId(1L);

        CoinCategory c1 = new CoinCategory();
        c1.setId(2L);
        c1.setUserId(2L);

        var testService = new CoinCategoryService(coinCategoryRepository);
        coinCategoryRepository.save(c);
        coinCategoryRepository.save(c1);

        assertFalse(testService.userIsOwner(1L,2L));
        assertFalse(testService.userIsOwner(2L,1L));
    }

    @Test
    void should_return_user_categories() {
        var coinCategoryRepository = inMemoryCoinCategoryRepository();

        CoinCategory c = new CoinCategory();
        c.setId(1L);
        c.setUserId(1L);

        CoinCategory c1 = new CoinCategory();
        c1.setId(2L);
        c1.setUserId(1L);

        CoinCategory c2 = new CoinCategory();
        c2.setId(3L);
        c2.setUserId(2L);

        var testService = new CoinCategoryService(coinCategoryRepository);
        coinCategoryRepository.save(c);
        coinCategoryRepository.save(c1);
        coinCategoryRepository.save(c2);

        assertEquals(testService.getUserCategories(1L).size(),2);
        assertEquals(testService.getUserCategories(2L).size(),1);
    }

    private CoinCategoryRepository inMemoryCoinCategoryRepository() {
        return new CoinCategoryRepository() {
            Map<Long, CoinCategory> categories = new HashMap<>();

            @Override
            public List<CoinCategory> findByUserId(Long id) {
                return categories.values().stream()
                        .filter(category -> category.getUserId().equals(id))
                        .collect(Collectors.toList());
            }

            @Override
            public Optional<CoinCategory> findById(Long id) {
                return Optional.ofNullable(categories.get(id));
            }

            @Override
            public boolean existsById(Long id) {
                return categories.values().stream()
                        .anyMatch(category -> category.getId().equals(id));
            }

            @Override
            public CoinCategory save(CoinCategory coin) {
                return categories.put(coin.getId(), coin);
            }

            @Override
            public void deleteById(Long id) {
                categories.remove(id);
            }
        };
    }


}