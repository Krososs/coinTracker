package pl.sk.coinTracker.CoinCategory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.sk.coinTracker.Security.AuthUtil;
import pl.sk.coinTracker.Support.Response;
import pl.sk.coinTracker.Support.Validation;
import pl.sk.coinTracker.User.UserService;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class CoinCategoryController {

    private final CoinCategoryService categoryService;
    private final UserService userService;

    public CoinCategoryController(CoinCategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @GetMapping("categories/get")
    public ResponseEntity<?> getUserCategories(Principal p) {
        Long userId = userService.getUserIdFromUsername(p.getName());
        return new ResponseEntity<>(categoryService.getUserCategories(userId), HttpStatus.OK);
    }

    @PostMapping("categories/create")
    public ResponseEntity<?> createCategory(@Valid CoinCategory category, Principal p, BindingResult result) {

        Long userId = userService.getUserIdFromUsername(p.getName());

        if (categoryService.categoryExists(userId, category.getName()))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.CATEGORY_ALREADY_EXIST.ToString()), HttpStatus.CONFLICT);
        if (result.hasErrors())
            return new ResponseEntity<>(Validation.getErrorResponse(result.getAllErrors()), HttpStatus.CONFLICT);

        category.setUserId(userId);
        categoryService.createNewCategory(category);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("categories/delete")
    public ResponseEntity<?> deleteCategory(@RequestParam Long categoryId, Principal p) {

        Long userId = userService.getUserIdFromUsername(p.getName());

        if (!categoryService.categoryExists(categoryId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.CATEGORY_DOES_NOT_EXIST.ToString()), HttpStatus.NOT_FOUND);
        if (!categoryService.userIsOwner(userId, categoryId))
            return new ResponseEntity<>(Validation.getErrorResponse(Response.USER_HAS_NO_RIGHTS_TO_CATEGORY.ToString()), HttpStatus.CONFLICT);

        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
