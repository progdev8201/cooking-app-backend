package cal.model.dto;

import cal.model.entity.RecipeToCook;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RecipeToCookDTO {
    private UUID id;
    private RecipeDTO recipe;
    private LocalDate cookDate;

    public RecipeToCookDTO() {

    }

    public RecipeToCookDTO(RecipeToCook recipeToCook){
        id = recipeToCook.getId();
        recipe = new RecipeDTO(recipeToCook.getRecipe());
        cookDate = recipeToCook.getCookDate();
    }
}
