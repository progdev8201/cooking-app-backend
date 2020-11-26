package cal.model.dto;

import cal.model.entity.Fridge;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class FridgeDTO {
    private UUID id;
    private List<RoutineArticleDTO> availableArticles;
    private List<RecipeDTO> availableRecipes;
    private List<RoutineArticleDTO> missingArticles;

    public FridgeDTO(){

    }

    public FridgeDTO(Fridge fridge){
        id = fridge.getId();

        availableArticles = fridge.getAvailableArticles().stream().map(RoutineArticleDTO::new).collect(Collectors.toList());

        availableRecipes = fridge.getAvailableRecipes().stream().map(RecipeDTO::new).collect(Collectors.toList());

        missingArticles = fridge.getMissingArticles().stream().map(RoutineArticleDTO::new).collect(Collectors.toList());
    }
}
