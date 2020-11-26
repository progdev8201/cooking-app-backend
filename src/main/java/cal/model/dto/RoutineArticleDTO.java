package cal.model.dto;

import cal.model.entity.RoutineArticle;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
public class RoutineArticleDTO implements Serializable,Comparable<RoutineArticleDTO> {
    private UUID id;

    @NotBlank
    private ArticleDTO article;

    @NotNull
    private int quantity;

    public RoutineArticleDTO(){

    }

    public RoutineArticleDTO(RoutineArticle routineArticle){
        this.id = routineArticle.getId();
        this.article = new ArticleDTO(routineArticle.getArticle());
        this.quantity = routineArticle.getQuantity();
    }

    @Override
    public int compareTo(RoutineArticleDTO o) {
        if (id.equals(o.getId()))
            return 0;
        else if (quantity > o.getQuantity())
            return 1;
        else
            return -1;
    }
}
