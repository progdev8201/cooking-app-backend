package cal.model.dto.response;

import cal.model.dto.RecipeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RecipeStringResponse {
    private UUID recipeId;
    private String recipeName;

    public RecipeStringResponse(RecipeDTO recipeDTO) {
        this.recipeId = recipeDTO.getId();
        this.recipeName = recipeDTO.getName();
    }
}