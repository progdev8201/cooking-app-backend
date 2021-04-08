package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.RecipeDTO;
import cal.model.dto.RoutineDTO;
import cal.model.dto.response.AllStatisticsResponse;
import cal.model.dto.response.CookingAmountPerMonthResponse;
import cal.model.dto.response.MoneySpentPerMonthResponse;
import cal.model.dto.response.RecipeCookTimePerMonthResponse;
import cal.model.entity.Recipe;
import cal.model.entity.RecipeToCook;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static cal.utility.EntityGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private final int COOKING_TRANSACTION_PER_MONTH_1_TO_6 = 2;
    private final int COOKING_TRANSACTION_PER_MONTH_7_TO_12 = 3;
    private final int ARTICLE_TRANSACTION_PER_MONTH_1_TO_6 = 2;
    private final int ARTICLE_TRANSACTION_PER_MONTH_7_TO_12 = 3;
    private final int AMOUNT_OF_RECIPE_TO_ADD = 2;
    private final int AMOUNT_OF_MONTHS = 12;
    private final int YEAR = 2021;
    private final int ROUTINE_INDEX = 0;

    @BeforeAll
    public void beforeAll() {
        // create user
        user = userRepository.save(setUpUserWithLogic());

        setUpCookingTransactionsOnRecipesWithLogic();

        setUpArticleTransactionsOnArticlesWithLogic();

        user = userRepository.findById(user.getUniqueId()).get();
    }

    // in this test we simply need to test the three private methods
    @Test
    public void findAllStatisticsTest(){
        // Arrange
        final UUID recipeId = user.getRecipes().get(0).getId();

        // Act
        AllStatisticsResponse allStatisticsResponse = statisticService.findAllStatistics(user.getUniqueId(),recipeId,YEAR);

        //todo how can i test this? because im only using stream methods and getting data from already tested methods
        // Assert
        assertTrue(allStatisticsResponse.getAverageMoneySpentPerMonth() > 0);
        assertTrue(allStatisticsResponse.getAverageTimeCookPerMonth() > 0);
        assertTrue(allStatisticsResponse.getMoneySpentThisYear() > 0);
    }

    @Test
    public void findMoneySpentPerMonthTest() {
        // Arrange
        final int articleAmountInRoutines = ARTICLE_AMOUNT / 2;
        final int amountOfBoughtArticleFirstHalf = ARTICLE_TRANSACTION_PER_MONTH_1_TO_6 * (AMOUNT_OF_MONTHS / 2) * articleAmountInRoutines;
        final int amountOfBoughtArticleSecondHalf = ARTICLE_TRANSACTION_PER_MONTH_7_TO_12 * (AMOUNT_OF_MONTHS / 2) * articleAmountInRoutines;
        final int totalAmountOfBoughtArticles = amountOfBoughtArticleFirstHalf + amountOfBoughtArticleSecondHalf;
        final float expectedPricePerMonthFirstHalf = ARTICLE_TRANSACTION_PER_MONTH_1_TO_6 * ARTICLE_BASIC_PRICE * articleAmountInRoutines;
        final float expectedPricePerMonthSecondHalf = ARTICLE_TRANSACTION_PER_MONTH_7_TO_12 * ARTICLE_BASIC_PRICE * articleAmountInRoutines;
        final float expectedMoneySpentForYear = ARTICLE_BASIC_PRICE * totalAmountOfBoughtArticles;

        // Act
        final List<MoneySpentPerMonthResponse> response = statisticService.findMoneySpentPerMonth(user.getUniqueId(), YEAR);

        final double totalMoneySpentInYear = response
                .stream()
                .mapToDouble(MoneySpentPerMonthResponse::getAmount)
                .sum();

        // Assert
        for (int i = 0; i < response.size(); i++) {
            if (i < 6)
                assertEquals(Math.round(expectedPricePerMonthFirstHalf), Math.round(response.get(i).getAmount()));
            else
                assertEquals(Math.round(expectedPricePerMonthSecondHalf), Math.round(response.get(i).getAmount()));
        }

        assertEquals(Math.round(expectedMoneySpentForYear), Math.round(totalMoneySpentInYear));
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
                assertEquals(COOKING_TRANSACTION_PER_MONTH_1_TO_6, response.get(i).getAmount());
            else
                assertEquals(COOKING_TRANSACTION_PER_MONTH_7_TO_12, response.get(i).getAmount());
        }
    }

    @Test
    public void findAmountOfTimeUserCookPerMonthTest() {
        // Arrange
        final int expectedAmountForFirst6Months = COOKING_TRANSACTION_PER_MONTH_1_TO_6 * AMOUNT_OF_RECIPE_TO_ADD;
        final int expectedAmountForLast6Months = COOKING_TRANSACTION_PER_MONTH_7_TO_12 * AMOUNT_OF_RECIPE_TO_ADD;

        // Act
        List<CookingAmountPerMonthResponse> response = statisticService.findAmountOfTimeUserCookPerMonth(user.getUniqueId(), YEAR);

        // Assert
        for (int i = 0; i < response.size(); i++) {
            if (i < 6)
                assertEquals(expectedAmountForFirst6Months, response.get(i).getAmount());
            else
                assertEquals(expectedAmountForLast6Months, response.get(i).getAmount());
        }
    }


    // PRIVATE METHODS

    // articles private methods

    private void setUpArticleTransactionsOnArticlesWithLogic() {
        RoutineDTO routineDTO = new RoutineDTO(user.getRoutines().get(ROUTINE_INDEX));

        for (int month = 1; month <= AMOUNT_OF_MONTHS; month++) {
            if (month < 7) {

                createArticleTransactionsOverMonths(routineDTO, ARTICLE_TRANSACTION_PER_MONTH_1_TO_6);

                final int jFirst6MonthsFormula = ARTICLE_TRANSACTION_PER_MONTH_1_TO_6 * month - ARTICLE_TRANSACTION_PER_MONTH_1_TO_6;
                final int jFirst6MonthsLimitFormula = ARTICLE_TRANSACTION_PER_MONTH_1_TO_6 * month;

                spreadMonthsLogicallyOnArticlesTransactions(routineDTO, jFirst6MonthsFormula, jFirst6MonthsLimitFormula, month);
            } else {
                createArticleTransactionsOverMonths(routineDTO, ARTICLE_TRANSACTION_PER_MONTH_7_TO_12);

                final int jLast6MonthsFormula = ARTICLE_TRANSACTION_PER_MONTH_7_TO_12 * month - 3 * ARTICLE_TRANSACTION_PER_MONTH_7_TO_12;
                final int jLast6MonthsLimitFormula = ARTICLE_TRANSACTION_PER_MONTH_7_TO_12 * month - 3 * ARTICLE_TRANSACTION_PER_MONTH_7_TO_12 + ARTICLE_TRANSACTION_PER_MONTH_7_TO_12;

                spreadMonthsLogicallyOnArticlesTransactions(routineDTO, jLast6MonthsFormula, jLast6MonthsLimitFormula, month);
            }
        }
    }

    private void spreadMonthsLogicallyOnArticlesTransactions(RoutineDTO routineDTO, int jFirst6MonthsFormula, int jFirst6MonthsLimitFormula, int monthI) {
        user = userRepository.findById(user.getUniqueId()).get();

        user.getArticles()
                .stream()
                .filter(article -> routineDTO.getRoutineArticles().stream().anyMatch(routineArticleDTO -> routineArticleDTO.getArticle().getId().equals(article.getId())))
                .forEach(article -> {
                    for (int j = jFirst6MonthsFormula; j < jFirst6MonthsLimitFormula; j++) {
                        article.getTransactions().get(j).setBougthDate(LocalDate.of(YEAR, monthI, 1));

                        articleService.update(new ArticleDTO(article), user.getUniqueId());
                    }
                });
    }

    private void createArticleTransactionsOverMonths(RoutineDTO routineDTO, int articleTransactPerMonth) {
        // update routine and put the right amount of transactions
        routineDTO.setRoutineArticles(
                routineDTO.getRoutineArticles()
                        .stream()
                        .map(routineArticleDTO -> {
                            routineArticleDTO.setQuantity(articleTransactPerMonth);
                            return routineArticleDTO;
                        })
                        .collect(Collectors.toList())
        );

        shoppingListService.addArticlesInShoppingList(user.getUniqueId(), routineDTO.getRoutineArticles());

        shoppingListService.shop(user.getUniqueId(), routineDTO.getRoutineArticles());
    }

    // recipes private methods

    private void setUpCookingTransactionsOnRecipesWithLogic() {
        List<Recipe> twoRecipes = user.getRecipes().subList(0, AMOUNT_OF_RECIPE_TO_ADD);

        for (int i = 1; i <= AMOUNT_OF_MONTHS; i++) {
            if (i < 7)
                createRecipeTransactionsOverMonth(twoRecipes, COOKING_TRANSACTION_PER_MONTH_1_TO_6);
            else
                createRecipeTransactionsOverMonth(twoRecipes, COOKING_TRANSACTION_PER_MONTH_7_TO_12);
        }

        for (int i = 1; i <= AMOUNT_OF_MONTHS; i++) {
            final int month = i;

            if (i < 7) {

                final int jFirst6MonthsFormula = COOKING_TRANSACTION_PER_MONTH_1_TO_6 * month - COOKING_TRANSACTION_PER_MONTH_1_TO_6;
                final int jFirst6MonthsLimitFormula = COOKING_TRANSACTION_PER_MONTH_1_TO_6 * month;

                addMonthsToCookingTransactions(twoRecipes, month, jFirst6MonthsFormula, jFirst6MonthsLimitFormula);
            } else {

                final int jLast6MonthsFormula = COOKING_TRANSACTION_PER_MONTH_7_TO_12 * month - 3 * COOKING_TRANSACTION_PER_MONTH_7_TO_12;
                final int jLast6MonthsLimitFormula = COOKING_TRANSACTION_PER_MONTH_7_TO_12 * month - 3 * COOKING_TRANSACTION_PER_MONTH_7_TO_12 + COOKING_TRANSACTION_PER_MONTH_7_TO_12;

                addMonthsToCookingTransactions(twoRecipes, month, jLast6MonthsFormula, jLast6MonthsLimitFormula);
            }
        }
    }

    private void addMonthsToCookingTransactions(List<Recipe> twoRecipes, int month, int firstIndex, int lastIndex) {
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