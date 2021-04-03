package cal.controller;

import cal.model.dto.AddRecipeToCookingListRequest;
import cal.model.dto.RecipeDTO;
import cal.model.dto.RecipeToCookDTO;
import cal.service.CookingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cooking")
public class CookingController {

    @Autowired
    private CookingListService cookingListService;

    @GetMapping("{userId}")
    public List<RecipeToCookDTO> findAll(@PathVariable final UUID userId) {
        return cookingListService.findAll(userId);
    }

    @GetMapping("{userId}/{recipeToCookId}")
    public void cookRecipe(@PathVariable final UUID userId, @PathVariable final UUID recipeToCookId) {
        cookingListService.cookRecipe(userId, recipeToCookId);
    }

    @PostMapping("{userId}")
    public void addRecipesToList(@PathVariable final UUID userId, @RequestBody final AddRecipeToCookingListRequest addRecipeToCookingListRequest) {
        cookingListService.addRecipesToList(userId, addRecipeToCookingListRequest.getRecipesToCook(), addRecipeToCookingListRequest.getCookDate());
    }

    @PutMapping("{userId}/{cookDate}/{recipeToCookId}")
    public RecipeToCookDTO updateCookDay(@PathVariable final UUID userId, @PathVariable final LocalDate cookDate, @PathVariable final UUID recipeToCookId) {
        return cookingListService.updateCookDay(userId, cookDate, recipeToCookId);
    }

    @PostMapping("delete/{userId}")
    public void deleteRecipes(@PathVariable final UUID userId, @RequestBody final List<UUID> recipesToDelete) {
        cookingListService.deleteRecipes(userId, recipesToDelete);
    }

}
