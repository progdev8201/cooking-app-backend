package cal.model.entity;

import cal.model.dto.RecipeDTO;
import cal.model.enums.RecipeType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Recipe implements Serializable {
    private UUID id;
    private String name;
    private List<RecipeArticle> recipeArticles;
    private String image;
    private String country;
    private String description;
    private int time;
    private RecipeType recipeType;
    private List<String> instructions;


    public Recipe() {
    }

    public Recipe(RecipeDTO recipeDTO) {
        id = recipeDTO.getId() == null ? UUID.randomUUID() : recipeDTO.getId();
        image = recipeDTO.getImage();
        country = recipeDTO.getCountry();
        description = recipeDTO.getDescription();
        recipeType = recipeDTO.getRecipeType();
        name = recipeDTO.getName();
        instructions = recipeDTO.getInstructions();
        time = recipeDTO.getTime();
        recipeArticles = new ArrayList<>();

        //map recipe articles

        recipeDTO.getRecipeArticles().stream().forEach(recipeArticleDTO -> recipeArticles.add(new RecipeArticle(recipeArticleDTO)));

    }

    public Recipe(UUID id, String name, List<RecipeArticle> recipeArticles, String image, String country, String description, RecipeType recipeType,int time) {
        this.id = id;
        this.recipeArticles = recipeArticles;
        this.image = image;
        this.country = country;
        this.description = description;
        this.recipeType = recipeType;
        this.name = name;
        this.time = time;
    }
}
