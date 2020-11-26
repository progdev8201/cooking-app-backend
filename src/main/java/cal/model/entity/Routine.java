package cal.model.entity;

import cal.model.dto.RoutineArticleDTO;
import cal.model.dto.RoutineDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class Routine implements Serializable {
    private UUID id;
    private String name;
    private List<RoutineArticle> routineArticles;

    public Routine() {
    }

    public Routine(RoutineDTO routineDTO){
        id = routineDTO.getId() == null ? UUID.randomUUID() : routineDTO.getId();
        name = routineDTO.getName();

        //map list

        routineArticles = routineDTO.getRoutineArticles().stream().map(RoutineArticle::new).collect(Collectors.toList());
    }

    public Routine(UUID id, String name, List<RoutineArticle> routineArticles) {
        this.id = id;
        this.name = name;
        this.routineArticles = routineArticles;
    }
}
