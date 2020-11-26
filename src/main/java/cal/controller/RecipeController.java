package cal.controller;

import cal.model.dto.RecipeDTO;
import cal.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @PostMapping("/{userId}")
    public RecipeDTO create(@Valid @RequestBody final RecipeDTO recipeDTO,@PathVariable final UUID userId){
        return recipeService.create(recipeDTO,userId);
    }

    @GetMapping("/{userId}/{recipeId}")
    public RecipeDTO find(@PathVariable final UUID userId,@PathVariable final UUID recipeId){
        return recipeService.find(userId,recipeId);
    }

    @GetMapping("/{userId}")
    public List<RecipeDTO> findAll(@PathVariable final UUID userId){
        return  recipeService.findAll(userId);
    }

    @PutMapping("/{userId}")
    public List<RecipeDTO> update(@PathVariable final UUID userId,@Valid @RequestBody final RecipeDTO recipeDTO){
        return recipeService.update(userId,recipeDTO);
    }

    @DeleteMapping("/{userId}/{recipeId}")
    public List<RecipeDTO> delete(@PathVariable final UUID userId,@PathVariable final UUID recipeId){
        return recipeService.delete(userId,recipeId);
    }
}
