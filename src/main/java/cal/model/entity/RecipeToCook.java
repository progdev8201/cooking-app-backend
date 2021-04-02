package cal.model.entity;

import cal.model.dto.RecipeToCookDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RecipeToCook implements Serializable {
    private UUID id;
    private Recipe recipe;
    private LocalDate cookDate;

    public RecipeToCook() {

    }

    public RecipeToCook(RecipeToCookDTO recipeToCookDTO) {
        id = recipeToCookDTO.getId() == null ? UUID.randomUUID() : recipeToCookDTO.getId();
        cookDate = recipeToCookDTO.getCookDate();
        recipe = new Recipe(recipeToCookDTO.getRecipe());
    }

    public RecipeToCook(UUID id, Recipe recipe, LocalDate cookDate) {
        this.id = id;
        this.recipe = recipe;
        this.cookDate = cookDate;
    }
}
