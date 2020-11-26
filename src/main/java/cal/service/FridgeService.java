package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.FridgeDTO;
import cal.model.dto.RecipeDTO;
import cal.model.entity.Fridge;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@Validated
public class FridgeService {
    private UserRepository userRepository;
    private RecipeService recipeService;
    private final Logger LOGGER = Logger.getLogger(FridgeService.class.getName());

    public FridgeService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.recipeService = new RecipeService(userRepository);
    }

    public FridgeDTO find(@NotNull UUID userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.isPresent() ? new FridgeDTO(user.get().getFridge()) : null;
    }

    public FridgeDTO update(@NotNull UUID userId, @Valid FridgeDTO fridgeDTO) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            u.setFridge(new Fridge(fridgeDTO));
            userRepository.save(u);

            LOGGER.info("FRIDGE UPDATED");
        });

        return find(userId);
    }

    public List<RecipeDTO> findAllCookable(@NotNull UUID userId) {
        List<ArticleDTO> validArticles = new ArrayList<>();

        find(userId).getAvailableArticles().stream().forEach(routineArticleDTO -> {
            routineArticleDTO.getArticle().setTransactions(new ArrayList<>());
            validArticles.add(routineArticleDTO.getArticle());
        });

        List<RecipeDTO> allRecipes = recipeService.findAll(userId);

        return findCookableRecipes(validArticles, allRecipes);
    }

    private List<RecipeDTO> findCookableRecipes(List<ArticleDTO> validArticles, List<RecipeDTO> allRecipes) {
        List<RecipeDTO> cookableRecipe = new ArrayList<>();

        allRecipes.stream().forEach(recipeDTO -> {
            List<ArticleDTO> recipeArticles = new ArrayList<>();

            recipeDTO.getRecipeArticles().forEach(recipeArticleDTO -> {
                recipeArticleDTO.getArticle().setTransactions(new ArrayList<>());
                recipeArticles.add(recipeArticleDTO.getArticle());
            });

            if (validArticles.containsAll(recipeArticles) && validArticles.size() > 0)
                cookableRecipe.add(recipeDTO);
        });

        return cookableRecipe;
    }

}
