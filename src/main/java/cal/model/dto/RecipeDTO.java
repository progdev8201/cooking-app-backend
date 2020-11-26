package cal.model.dto;

import cal.model.entity.Recipe;
import cal.model.enums.RecipeType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public RecipeDTO(){

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
        recipeArticles = new ArrayList<>();

        //map recipe articles

        recipe.getRecipeArticles().stream().forEach(recipeArticle -> recipeArticles.add(new RecipeArticleDTO(recipeArticle)));
    }
}
