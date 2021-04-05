package cal.service;

import cal.model.dto.RecipeDTO;
import cal.model.entity.CookingTransaction;
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
import java.util.stream.Collectors;

import static cal.utility.EntityGenerator.setUpUserWithLogic;

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

    private final int TRANSACTION_PER_MONTH_1_TO_6 = 5;
    private final int TRANSACTION_PER_MONTH_7_TO_12 = 10;
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
                createRecipeTransactionsOverMonth(twoRecipes, i, TRANSACTION_PER_MONTH_1_TO_6);
            else
                createRecipeTransactionsOverMonth(twoRecipes, i, TRANSACTION_PER_MONTH_7_TO_12);
        }

        // you should get the user back from db after this

        // cook them it should create 3 transactions

        // add transactions over month for recipes
        user = userRepository.findById(user.getUniqueId()).get();
    }

    @Test
    public void test() {
        System.out.println(user);
    }

    private void createRecipeTransactionsOverMonth(List<Recipe> threeRecipes, int i, int transactionPerMonth) {
        for (int j = 0; j < transactionPerMonth; j++) {
            final int month = i;
            // map the three recipes to recipes dto
            List<RecipeDTO> allRecipes = threeRecipes
                    .stream()
                    .map(RecipeDTO::new)
                    .collect(Collectors.toList());

            // add recipe to cooking list with cook date being equal to the month
            cookingListService.addRecipesToList(user.getUniqueId(), allRecipes, LocalDate.now());

            // fetch user
            user = userRepository.findById(user.getUniqueId()).get();

            // fetch recipes to cook
            List<RecipeToCook> recipesToCook = user.getCookingList();

            recipesToCook.forEach(recipe -> cookingListService.cookRecipe(user.getUniqueId(), recipe.getId()));

            // fetch user again
            user = userRepository.findById(user.getUniqueId()).get();

            // fetch all three recipes then edit cooking transaction date for each recipes by putting the right month
            user.getRecipes()
                    .stream()
                    .filter(recipe -> allRecipes.stream().anyMatch(recipeDTO -> recipeDTO.getId().equals(recipe.getId())))
                    .forEach(recipe -> {
                        // update all transactions
                        List<CookingTransaction> allTransactionsUpdated = recipe.getCookingTransactions()
                                .stream()
                                .filter(cookingTransaction -> cookingTransaction.getCookDate().getMonth().getValue() != month - 1)
                                .map(cookingTransaction -> {
                                    cookingTransaction.setCookDate(LocalDate.of(YEAR, month, 1));
                                    return cookingTransaction;
                                })
                                .collect(Collectors.toList());

                        // set to recipe
                        recipe.setCookingTransactions(allTransactionsUpdated);

                        // update recipe
                        recipeService.update(user.getUniqueId(), new RecipeDTO(recipe));
                    });
        }
    }
}