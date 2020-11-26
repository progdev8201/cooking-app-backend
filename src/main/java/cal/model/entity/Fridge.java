package cal.model.entity;

import cal.model.dto.FridgeDTO;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Fridge {
    private UUID id;
    private List<RoutineArticle> availableArticles;
    private List<Recipe> availableRecipes;
    private List<RoutineArticle> missingArticles;

    public Fridge() {
        id = UUID.randomUUID();
        availableArticles = new ArrayList<>();
        availableRecipes = new ArrayList<>();
        missingArticles = new ArrayList<>();
    }

    public Fridge(FridgeDTO fridgeDTO) {
        id = fridgeDTO.getId() == null ? UUID.randomUUID() : fridgeDTO.getId();

        availableArticles = fridgeDTO.getAvailableArticles().stream().map(RoutineArticle::new).collect(Collectors.toList());

        availableRecipes = fridgeDTO.getAvailableRecipes().stream().map(Recipe::new).collect(Collectors.toList());

        missingArticles = fridgeDTO.getMissingArticles().stream().map(RoutineArticle::new).collect(Collectors.toList());
    }
}
