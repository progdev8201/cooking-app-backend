package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.UserDTO;
import cal.model.entity.Article;
import cal.model.entity.RecipeArticle;
import cal.model.entity.RoutineArticle;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@Validated
public class ArticleService {

    private UserRepository userRepository;

    private final Logger LOGGER = Logger.getLogger(ArticleService.class.getName());

    public ArticleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ArticleDTO create(@Valid ArticleDTO articleDTO, UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        Article article = new Article(articleDTO);

        if (user.isPresent()) {
            user.get().getArticles().add(article);
            userRepository.save(user.get());
            LOGGER.info("NEW ARTICLE CREATED");
        }

        return find(userId, article.getId());
    }

    public List<ArticleDTO> findAll(UUID userId) {
        Optional<UserDTO> userDTO = userRepository.findById(userId).stream().map(UserDTO::new).findFirst();

        return userDTO.isPresent() ? userDTO.get().getArticles() : null;
    }

    public ArticleDTO find(UUID userId, UUID articleId) {
        return findAll(userId).stream().filter(articleDTO -> articleDTO.getId().equals(articleId)).findFirst().orElse(null);
    }

    public ArticleDTO update(@Valid ArticleDTO articleDTO, UUID userId) {
        // find user then update his article then save the client then return the article
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {

            //  update in user article list
            Optional<Article> articleToUpdate = u.getArticles().stream().filter(article -> article.getId().equals(articleDTO.getId())).findFirst();

            articleToUpdate.ifPresent(article -> {
                u.getArticles().set(u.getArticles().indexOf(article), new Article(articleDTO));
                LOGGER.info("ARTICLE UPDATED");
            });

            // update in user routines
            u.getRoutines().forEach(routine -> {
                Optional<RoutineArticle> routineArticleToUpdate = routine.getRoutineArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleDTO.getId())).findFirst();

                routineArticleToUpdate.ifPresent(routineArticle -> {
                    routine.getRoutineArticles().get(routine.getRoutineArticles().indexOf(routineArticle)).setArticle(new Article(articleDTO));
                    LOGGER.info("ARTICLE UPDATED IN ROUTINE");
                });
            });

            // update in shopping list
            Optional<RoutineArticle> routineArticleToUpdate = u.getShoppingList().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleDTO.getId())).findFirst();

            routineArticleToUpdate.ifPresent(routineArticle -> {
                u.getShoppingList().get(u.getShoppingList().indexOf(routineArticle)).setArticle(new Article(articleDTO));
                LOGGER.info("ARTICLE UPDATED IN SHOPPING LIST");
            });

            // update in user recipes
            u.getRecipes().forEach(recipe -> {
                Optional<RecipeArticle> recipeArticleToUpdate = recipe.getRecipeArticles().stream().filter(recipeArticle -> recipeArticle.getArticle().getId().equals(articleDTO.getId())).findFirst();

                recipeArticleToUpdate.ifPresent(recipeArticle -> {
                    recipe.getRecipeArticles().get(recipe.getRecipeArticles().indexOf(recipeArticle)).setArticle(new Article(articleDTO));
                    LOGGER.info("ARTICLE UPDATED IN RECIPE");
                });
            });

            // update in user fridge available recipes
            u.getFridge().getAvailableRecipes().forEach(recipe -> {
                Optional<RecipeArticle> recipeArticleToUpdate = recipe.getRecipeArticles().stream().filter(recipeArticle -> recipeArticle.getArticle().getId().equals(articleDTO.getId())).findFirst();

                recipeArticleToUpdate.ifPresent(recipeArticle -> {
                    recipe.getRecipeArticles().get(recipe.getRecipeArticles().indexOf(recipeArticle)).setArticle(new Article(articleDTO));
                    LOGGER.info("ARTICLE UPDATED IN FRIDGE AVAILABLE RECIPE");
                });
            });

            // update in user fridge available articles
            Optional<RoutineArticle> availableRoutineArticleToUpdate = u.getFridge().getAvailableArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleDTO.getId())).findFirst();

            availableRoutineArticleToUpdate.ifPresent(routineArticle -> {
                final int routineArticleIndex = u.getFridge().getAvailableArticles().indexOf(routineArticle);
                u.getFridge().getAvailableArticles().get(routineArticleIndex).setArticle(new Article(articleDTO));
                LOGGER.info("ARTICLE UPDATED IN FRIDGE AVAILABLE ARTICLES");
            });

            // update in user fridge missing articles
            Optional<RoutineArticle> missingRoutineArticleToUpdate = u.getFridge().getMissingArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleDTO.getId())).findFirst();

            missingRoutineArticleToUpdate.ifPresent(routineArticle -> {
                final int routineArticleIndex = u.getFridge().getMissingArticles().indexOf(routineArticle);
                u.getFridge().getMissingArticles().get(routineArticleIndex).setArticle(new Article(articleDTO));
                LOGGER.info("ARTICLE UPDATED IN FRIDGE MISSING ARTICLES");
            });

            userRepository.save(u);
        });

        return find(userId, articleDTO.getId());
    }

    public void delete(UUID articleId, UUID userId) {
        // find user then find article list then delete article then save user
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            deleteInAllEntity(articleId, u);

            userRepository.save(u);
        });
    }

    private void deleteInAllEntity(UUID articleId, User u) {
        // check in user article list
        deleteInAllUserArticles(articleId, u);

        // check all user recipes
        deleteInAllUserRecipes(articleId, u);

        // check all user routines
        deleteInAllUserRoutine(articleId, u);

        // check all user fridge available articles
        deleteInAvalaibleFridgeArticle(articleId, u);

        // check all user fridge missing articles
        deleteInMissingFridgeArticle(articleId, u);

        // check all user fridge available recipe
        deleteInAvailableFridgeRecipe(articleId, u);

        // check all routine articles in shopping list
        deleteInShoppingList(articleId, u);
    }

    public List<String> findAllOccurences(UUID userId, UUID articleId) {
        //find user
        List<String> placesFound = new ArrayList<>();
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            boolean articlePresent = u.getArticles().stream().filter(article -> article.getId().equals(articleId)).findFirst().isPresent();

            if (articlePresent)
                checkAllEntitiesForArticle(articleId, placesFound, u);
        });

        return placesFound;
    }

    // private utility methods

    private void deleteInShoppingList(UUID articleId, User u) {
        Optional<RoutineArticle> routineArticleToDelete = u.getShoppingList().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleId)).findFirst();
        routineArticleToDelete.ifPresent(routineArticle -> {
            u.getShoppingList().remove(routineArticle);
            LOGGER.info("NEW ARTICLE DELETED IN SHOPPING LIST");
        });
    }

    private void deleteInAllUserArticles(UUID articleId, User u) {
        Optional<Article> articleToDelete = u.getArticles().stream().filter(article -> article.getId().equals(articleId)).findFirst();

        articleToDelete.ifPresent(article -> {
            u.getArticles().remove(article);
            LOGGER.info("NEW ARTICLE DELETED");
        });
    }

    private void deleteInAllUserRecipes(UUID articleId, User u) {
        u.getRecipes().forEach(recipe -> {
            Optional<RecipeArticle> recipeArticleToDelete = recipe.getRecipeArticles().stream().filter(recipeArticle -> recipeArticle.getArticle().getId().equals(articleId)).findFirst();

            recipeArticleToDelete.ifPresent(recipeArticle -> {
                recipe.getRecipeArticles().remove(recipeArticle);
                LOGGER.info("NEW ARTICLE DELETED IN USER RECIPE");
            });
        });
    }

    private void deleteInAllUserRoutine(UUID articleId, User u) {
        u.getRoutines().forEach(routine -> {
            Optional<RoutineArticle> routineArticleToDelete = routine.getRoutineArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleId)).findFirst();

            routineArticleToDelete.ifPresent(routineArticle -> {
                routine.getRoutineArticles().remove(routineArticle);
                LOGGER.info("NEW ARTICLE DELETED IN USER ROUTINE");
            });
        });
    }

    private void deleteInAvalaibleFridgeArticle(UUID articleId, User u) {
        Optional<RoutineArticle> availableArticleToDelete = u.getFridge().getAvailableArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleId)).findFirst();

        availableArticleToDelete.ifPresent(routineArticle -> {
            u.getFridge().getAvailableArticles().remove(routineArticle);
            LOGGER.info("NEW ARTICLE DELETED IN FRIDGE AVAILABLE ARTICLES");
        });
    }

    private void deleteInMissingFridgeArticle(UUID articleId, User u) {
        Optional<RoutineArticle> missingArticleToDelete = u.getFridge().getMissingArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleId)).findFirst();

        missingArticleToDelete.ifPresent(routineArticle -> {
            u.getFridge().getMissingArticles().remove(routineArticle);
            LOGGER.info("NEW ARTICLE DELETED IN FRIDGE MISSING ARTICLES");
        });
    }

    private void deleteInAvailableFridgeRecipe(UUID articleId, User u) {
        u.getFridge().getAvailableRecipes().forEach(recipe -> {
            Optional<RecipeArticle> recipeArticleToDelete = recipe.getRecipeArticles().stream().filter(recipeArticle -> recipeArticle.getArticle().getId().equals(articleId)).findFirst();

            recipeArticleToDelete.ifPresent(recipeArticle -> {
                recipe.getRecipeArticles().remove(recipeArticle);
                LOGGER.info("NEW ARTICLE DELETED IN FRIDGE RECIPE");
            });
        });
    }

    private void checkAllEntitiesForArticle(UUID articleId, List<String> placesFound, User u) {
        // check all user routines
        checkAllRoutineForArticle(articleId, placesFound, u);

        // check all user recipes
        CheckAllUserRecipesForArticle(articleId, placesFound, u);

        // check all available article in fridge
        checkAllFridgeAvailableArticlesForArticle(articleId, placesFound, u);

        // check all missing article in fridge
        checkAllFridgeMissingArticlesForArticle(articleId, placesFound, u);

        // check all available recipes in fridge
        checkAllFridgeAvailableRecipesForArticle(articleId, placesFound, u);

        // check all routine article in shopping list
        checkShoppingListForArticle(articleId, u,placesFound);
    }

    private void checkShoppingListForArticle(UUID articleId, User u,List<String> placesFound) {
        Optional<RoutineArticle> routineArticleToDelete = u.getShoppingList().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleId)).findFirst();
        routineArticleToDelete.ifPresent(routineArticle -> {
            placesFound.add("shopping list");
        });
    }

    private void checkAllFridgeAvailableRecipesForArticle(UUID articleId, List<String> placesFound, User u) {
        u.getFridge().getAvailableRecipes()
                .stream()
                .forEach(recipe -> {
                    Optional<RecipeArticle> recipeArticleToDelete = recipe.getRecipeArticles().stream().filter(recipeArticle -> recipeArticle.getArticle().getId().equals(articleId)).findFirst();
                    recipeArticleToDelete.ifPresent(recipeArticle -> {
                        placesFound.add("fridge available recipe: " + recipe.getName());
                    });
                });
    }

    private void checkAllFridgeMissingArticlesForArticle(UUID articleId, List<String> placesFound, User u) {
        Optional<RoutineArticle> missingRoutineArticle = u.getFridge().getMissingArticles().stream().filter(article -> article.getArticle().getId().equals(articleId)).findFirst();
        if (missingRoutineArticle.isPresent())
            placesFound.add("fridge missing article");
    }

    private void checkAllFridgeAvailableArticlesForArticle(UUID articleId, List<String> placesFound, User u) {
        Optional<RoutineArticle> availableRoutineArticle = u.getFridge().getAvailableArticles().stream().filter(article -> article.getArticle().getId().equals(articleId)).findFirst();
        if (availableRoutineArticle.isPresent())
            placesFound.add("fridge available article");
    }

    private void CheckAllUserRecipesForArticle(UUID articleId, List<String> placesFound, User u) {
        u.getRecipes()
                .stream()
                .forEach(recipe -> {
                    if (recipe.getRecipeArticles().stream().filter(recipeArticle -> recipeArticle.getArticle().getId().equals(articleId)).findFirst().isPresent())
                        placesFound.add("recipe: " + recipe.getName());
                });
    }

    private void checkAllRoutineForArticle(UUID articleId, List<String> placesFound, User u) {
        u.getRoutines()
                .stream()
                .forEach(routine -> {
                    if (routine.getRoutineArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleId)).findFirst().isPresent())
                        placesFound.add("routine: " + routine.getName());
                });
    }
}
