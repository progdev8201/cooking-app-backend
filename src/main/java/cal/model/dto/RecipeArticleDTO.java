package cal.model.dto;

import cal.model.entity.RecipeArticle;
import cal.model.enums.UnitMeasurement;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RecipeArticleDTO implements Serializable {
    private UUID id;
    private ArticleDTO article;
    private String amount;
    private UnitMeasurement measurement;

    public RecipeArticleDTO(){

    }

    public RecipeArticleDTO(RecipeArticle recipeArticle) {
        id = recipeArticle.getId();
        article = new ArticleDTO(recipeArticle.getArticle());
        amount = recipeArticle.getAmount();
        measurement = recipeArticle.getMeasurement();
    }
}
