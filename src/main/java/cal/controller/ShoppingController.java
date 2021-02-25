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

    @PutMapping("{userId}")
    public List<RoutineArticle> updateShoppingList(@PathVariable final UUID userId, @RequestBody Set<RoutineArticleDTO> shoppingListArticles) {
        return shoppingListService.updateShoppingList(userId, shoppingListArticles);
    }

    @GetMapping("{userId}")
    public List<RoutineArticle> find(@PathVariable final UUID userId) {
        return shoppingListService.find(userId);
    }

    @GetMapping("shopAll/{userId}")
    public void shop(@PathVariable final UUID userId) {
        shoppingListService.shop(userId);
    }
}
