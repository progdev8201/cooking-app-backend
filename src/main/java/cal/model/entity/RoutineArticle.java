package cal.model.entity;

import cal.model.dto.RoutineArticleDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RoutineArticle implements Serializable, Comparable<RoutineArticle> {
    private UUID id;
    private Article article;
    private int quantity;

    public RoutineArticle() {

    }

    public RoutineArticle(RoutineArticleDTO routineArticleDTO) {
        id = routineArticleDTO.getId() == null ? UUID.randomUUID() : routineArticleDTO.getId();
        article = new Article(routineArticleDTO.getArticle());
        quantity = routineArticleDTO.getQuantity();
    }

    public RoutineArticle(UUID id, Article article, int quantity) {
        this.id = id;
        this.article = article;
        this.quantity = quantity;
    }

    @Override
    public int compareTo(RoutineArticle o) {
        if (id.equals(o.getId()))
            return 0;
        else if (quantity > o.getQuantity())
            return 1;
        else
            return -1;
    }
}
