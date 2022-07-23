package pl.sk.coinTracker.CategoryContent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sk.coinTracker.Coin.CoinService;
import pl.sk.coinTracker.CoinCategory.CoinCategory;
import pl.sk.coinTracker.CoinCategory.CoinCategoryService;
import pl.sk.coinTracker.Security.AuthUtil;
import pl.sk.coinTracker.Support.Response;
import pl.sk.coinTracker.Support.Validation;
import pl.sk.coinTracker.User.UserService;

import java.util.List;

@RestController
public class CategoryContentController {

    private final CategoryContentService contentService;
    private final CoinCategoryService categoryService;
    private final UserService userService;
    private final CoinService coinService;

    public CategoryContentController(CategoryContentService contentService, CoinCategoryService categoryService, UserService userService, CoinService coinService) {
        this.contentService = contentService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.coinService = coinService;
    }

    @GetMapping("categories/content/get")
    public ResponseEntity<?> getContent(@RequestParam Long categoryId, @RequestHeader("authorization") String token) {
        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));
        if(!categoryService.categoryExists(categoryId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.CATEGORY_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!categoryService.userIsOwner(userId, categoryId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_CATEGORY.ToString()), HttpStatus.CONFLICT);

        List<CategoryContent> contentList = contentService.getByCategoryId(categoryId);

        return new ResponseEntity<>(contentList,HttpStatus.OK);
    }

    @PostMapping("categories/content/add")
    public ResponseEntity<?> categorizeCoin(@RequestParam Long coinId, @RequestParam Long categoryId,@RequestHeader("authorization") String token) {
        CategoryContent content = new CategoryContent(categoryId, coinId);
        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));

        if(!categoryService.categoryExists(categoryId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.CATEGORY_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!coinService.coinExistsById(coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!categoryService.userIsOwner(userId, categoryId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_CATEGORY.ToString()), HttpStatus.CONFLICT);
        if(contentService.categorized(categoryId, coinId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.COIN_ALREADY_CATEGORIZED.ToString()), HttpStatus.CONFLICT);

        contentService.categorize(content);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("categories/content/delete")
    public ResponseEntity<?> decategorizeCoin(@RequestParam Long contentId,@RequestHeader("authorization") String token) {

        Long userId = userService.getUserIdFromUsername(AuthUtil.getUsernameFromToken(token));

        if(!contentService.contentExists(contentId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.CATEGORY_CONTENT_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        Long categoryId = contentService.getById(contentId).getCategoryId();
        if(!categoryService.userIsOwner(userId,categoryId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_CATEGORY.ToString()), HttpStatus.CONFLICT);

        contentService.decategorize(contentId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
