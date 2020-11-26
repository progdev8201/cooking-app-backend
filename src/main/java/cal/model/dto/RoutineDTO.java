package cal.model.dto;

import cal.model.entity.Routine;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class RoutineDTO implements Serializable {
    private UUID id;

    @NotBlank
    private String name;

    private List<RoutineArticleDTO> routineArticles;

    public RoutineDTO(){

    }

    public RoutineDTO(Routine routine){
        id = routine.getId();
        name = routine.getName();

        //map list

        routineArticles = routine.getRoutineArticles().stream().map(RoutineArticleDTO::new).collect(Collectors.toList());
    }
}
