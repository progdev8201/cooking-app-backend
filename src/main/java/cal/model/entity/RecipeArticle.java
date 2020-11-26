package cal.model.entity;

import cal.model.dto.RecipeArticleDTO;
import cal.model.enums.UnitMeasurement;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RecipeArticle implements Serializable {
    private UUID id;
    private Article article;
    private String amount;
    private UnitMeasurement measurement;

    public RecipeArticle() {
    }

    public RecipeArticle(RecipeArticleDTO recipeArticleDTO) {
        this.id = recipeArticleDTO.getId() == null ? UUID.randomUUID() : recipeArticleDTO.getId();
        this.article = new Article(recipeArticleDTO.getArticle());
        this.amount = recipeArticleDTO.getAmount();
        this.measurement = recipeArticleDTO.getMeasurement();
    }

    public RecipeArticle(UUID id, Article article, String amount, UnitMeasurement measurement) {
        this.id = id;
        this.article = article;
        this.amount = amount;
        this.measurement = measurement;
    }
}
