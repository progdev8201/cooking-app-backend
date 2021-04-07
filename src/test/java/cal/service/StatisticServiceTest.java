package cal.service;

import cal.model.dto.RecipeDTO;
import cal.model.dto.response.CookingAmountPerMonthResponse;
import cal.model.dto.response.RecipeCookTimePerMonthResponse;
import cal.model.entity.Recipe;
import cal.model.entity.RecipeToCook;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import({StatisticService.class, CookingListService.class, ShoppingListService.class, RecipeService.class, ArticleService.class, ImageService.class})
public class StatisticServiceTest {

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CookingListService cookingListService;

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private final int TRANSACTION_PER_MONTH_1_TO_6 = 2;
    private final int TRANSACTION_PER_MONTH_7_TO_12 = 3;
    private final int AMOUNT_OF_RECIPE_TO_ADD = 2;
    private final int AMOUNT_OF_MONTHS = 12;
    private final int YEAR = 2021;

    @BeforeEach
    public void beforeEach() {
        // create user
        user = userRepository.save(setUpUserWithLogic());

        // add two recipes with different dates to cooking list with service
        List<Recipe> twoRecipes = user.getRecipes().subList(0, AMOUNT_OF_RECIPE_TO_ADD);

        for (int i = 1; i <= AMOUNT_OF_MONTHS; i++) {
            if (i < 7)
                createRecipeTransactionsOverMonth(twoRecipes, TRANSACTION_PER_MONTH_1_TO_6);
            else
                createRecipeTransactionsOverMonth(twoRecipes, TRANSACTION_PER_MONTH_7_TO_12);
        }

        for (int i = 1; i <= AMOUNT_OF_MONTHS; i++) {
            final int month = i;

            if (i < 7) {

                final int jFirst6MonthsFormula = TRANSACTION_PER_MONTH_1_TO_6 * month - TRANSACTION_PER_MONTH_1_TO_6;
                final int jFirst6MonthsLimitFormula = TRANSACTION_PER_MONTH_1_TO_6 * month;

                addMonthsToTransaction(twoRecipes, month, jFirst6MonthsFormula, jFirst6MonthsLimitFormula);
            } else {

                final int jLast6MonthsFormula = TRANSACTION_PER_MONTH_7_TO_12 * month - 3 * TRANSACTION_PER_MONTH_7_TO_12;
                final int jLast6MonthsLimitFormula = TRANSACTION_PER_MONTH_7_TO_12 * month - 3 * TRANSACTION_PER_MONTH_7_TO_12 + TRANSACTION_PER_MONTH_7_TO_12;

                addMonthsToTransaction(twoRecipes, month, jLast6MonthsFormula, jLast6MonthsLimitFormula);
            }
        }

        // you should get the user back from db after this

        // cook them it should create 3 transactions
        System.out.println(user);

    }

    @Test
    public void findAmountOfTimeARecipeIsCookedPerMonthTest() {
        // Arrange
        final UUID recipeId = user.getRecipes().get(0).getId();

        // Act
        List<RecipeCookTimePerMonthResponse> response = statisticService.findAmountOfTimeARecipeIsCookedPerMonth(user.getUniqueId(), recipeId, YEAR);

        // Assert
        for (int i = 0; i < response.size(); i++) {
            if (i < 6)
                assertEquals(TRANSACTION_PER_MONTH_1_TO_6, response.get(i).getAmount());
            else
                assertEquals(TRANSACTION_PER_MONTH_7_TO_12, response.get(i).getAmount());
        }
    }

    @Test
    public void findAmountOfTimeUserCookPerMonthTest(){
        // Arrange
        final int expectedAmountForFirst6Months = TRANSACTION_PER_MONTH_1_TO_6 * AMOUNT_OF_RECIPE_TO_ADD;
        final int expectedAmountForLast6Months = TRANSACTION_PER_MONTH_7_TO_12 * AMOUNT_OF_RECIPE_TO_ADD;
        // Act
        List<CookingAmountPerMonthResponse> response = statisticService.findAmountOfTimeUserCookPerMonth(user.getUniqueId(),YEAR);

        // Assert
        for (int i = 0; i < response.size(); i++) {
            if (i < 6)
                assertEquals(expectedAmountForFirst6Months, response.get(i).getAmount());
            else
                assertEquals(expectedAmountForLast6Months, response.get(i).getAmount());
        }
    }

    private void addMonthsToTransaction(List<Recipe> twoRecipes, int month, int firstIndex, int lastIndex) {
        // prendre chaque recette + prendre les transaction de tel index a tel index et mettre le bon mois dedans
        user.getRecipes()
                .stream()
                .filter(recipe -> twoRecipes.stream().anyMatch(recipeDTO -> recipeDTO.getId().equals(recipe.getId())))
                .forEach(recipe -> {

                    // update all transactions
                    for (int j = firstIndex; j < lastIndex; j++) {
                        recipe.getCookingTransactions().get(j).setCookDate(LocalDate.of(YEAR, month, 1));
                    }

                    // update recipe
                    recipeService.update(user.getUniqueId(), new RecipeDTO(recipe));
                });
    }

    private void createRecipeTransactionsOverMonth(List<Recipe> twoRecipes, int transactionPerMonth) {
        // map the three recipes to recipes dto
        List<RecipeDTO> allRecipes = twoRecipes
                .stream()
                .map(RecipeDTO::new)
                .collect(Collectors.toList());

        for (int j = 0; j < transactionPerMonth; j++) {

            // add recipe to cooking list with cook date being equal to the month
            cookingListService.addRecipesToList(user.getUniqueId(), allRecipes, LocalDate.now());

            // fetch user
            user = userRepository.findById(user.getUniqueId()).get();

            // fetch recipes to cook
            List<RecipeToCook> recipesToCook = user.getCookingList();

            recipesToCook.forEach(recipe -> cookingListService.cookRecipe(user.getUniqueId(), recipe.getId()));

        }

    }
}