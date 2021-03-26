package cal.controller;

import cal.model.dto.RoutineArticleDTO;
import cal.model.entity.RoutineArticle;
import cal.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/shop")
public class ShoppingController {

    @Autowired
    private ShoppingListService shoppingListService;

    @PutMapping("add/{userId}")
    public List<RoutineArticle> addArticlesInShoppingList(@PathVariable final UUID userId, @RequestBody List<RoutineArticleDTO> articlesToAdd){
        return shoppingListService.addArticlesInShoppingList(userId,articlesToAdd);
    }

    @PutMapping("delete/{userId}")
    public List<RoutineArticle> deleteArticlesInShoppingList(@PathVariable final UUID userId, @RequestBody List<RoutineArticleDTO> articlesToDelete){
        return shoppingListService.deleteArticlesInShoppingList(userId,articlesToDelete);
    }

    @GetMapping("{userId}")
    public List<RoutineArticle> find(@PathVariable final UUID userId) {
        return shoppingListService.find(userId);
    }

    @PostMapping("shopAll/{userId}")
    public void shop(@PathVariable final UUID userId,@RequestBody final List<RoutineArticleDTO> articlesToShop) {
        shoppingListService.shop(userId,articlesToShop);
    }
}
