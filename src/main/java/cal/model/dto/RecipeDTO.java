package cal.model.dto;

import cal.model.entity.Recipe;
import cal.model.enums.RecipeType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class RecipeDTO implements Serializable {
    private UUID id;
    @NotBlank
    private String name;
    @NotNull
    private List<RecipeArticleDTO> recipeArticles;
    private String image;
    private String country;
    private String description;
    private int time;
    @NotNull
    private RecipeType recipeType;
    private List<String> instructions;
    private List<CookingTransactionDTO> cookingTransactions;

    public RecipeDTO() {

    }

    public RecipeDTO(Recipe recipe) {
        id = recipe.getId();
        name = recipe.getName();
        image = recipe.getImage();
        country = recipe.getCountry();
        description = recipe.getDescription();
        recipeType = recipe.getRecipeType();
        instructions = recipe.getInstructions();
        time = recipe.getTime();

        //map recipe articles

        recipeArticles = recipe.getRecipeArticles().stream().map(RecipeArticleDTO::new).collect(Collectors.toList());
        cookingTransactions = recipe.getCookingTransactions().stream().map(CookingTransactionDTO::new).collect(Collectors.toList());
    }
}
