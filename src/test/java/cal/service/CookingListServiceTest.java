package cal.service;

import cal.model.dto.RecipeDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import(CookingListService.class)
public class CookingListServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CookingListService cookingListService;

    private User user;

    @BeforeEach
    public void beforeEach() {
        user = userRepository.save(setUpUserWithLogic());
    }

    @Test
    public void findAll() {
        // Arrange

        // Act
        List<RecipeToCook> recipesToCook = cookingListService.findAll(user.getUniqueId())
                .stream()
                .map(RecipeToCook::new)
                .collect(Collectors.toList());

        // Assert
        assertEquals(recipesToCook, user.getCookingList());

    }

    @Test
    public void addRecipesToListTest() {
        // Arrange
        final int initialSize = user.getCookingList().size();

        final int finalSize = initialSize + 1;

        final RecipeDTO recipeToAdd = new RecipeDTO(user.getRecipes().get(0));

        // Act
        cookingListService.addRecipesToList(user.getUniqueId(), Arrays.asList(recipeToAdd), LocalDate.now());

        user = userRepository.findById(user.getUniqueId()).get();

        // Assert
        assertEquals(finalSize, user.getCookingList().size());
        assertEquals(new Recipe(recipeToAdd), user.getCookingList().get(finalSize - 1).getRecipe());
    }

    @Test
    public void updateCookDayTest() {
        // Arrange
        final int indexOfRecipe = 0;

        final RecipeToCook recipeToCookToUpdate = user.getCookingList().get(indexOfRecipe);

        final LocalDate timeToAdd = LocalDate.now().minusDays(6l);

        // Act
        cookingListService.updateCookDay(user.getUniqueId(), timeToAdd, recipeToCookToUpdate.getId());

        user = userRepository.findById(user.getUniqueId()).get();

        final RecipeToCook recipeToCookUpdated = user.getCookingList().get(indexOfRecipe);

        // Assert
        assertNotEquals(recipeToCookToUpdate.getCookDate(), recipeToCookUpdated.getCookDate());
        assertEquals(timeToAdd, recipeToCookUpdated.getCookDate());
        assertEquals(recipeToCookToUpdate.getRecipe(), recipeToCookUpdated.getRecipe());
        assertEquals(recipeToCookToUpdate.getId(), recipeToCookUpdated.getId());
    }

    @Test
    public void deleteRecipesTest() {
        // Arrange
        final int recipeToCookIndex = 0;
        final int cookingListSize = user.getCookingList().size();
        final int expectedCookingSize = cookingListSize - 1;

        final RecipeToCook recipeToDelete = user.getCookingList().get(recipeToCookIndex);

        // Act
        cookingListService.deleteRecipes(user.getUniqueId(), Arrays.asList(recipeToDelete.getId()));

        user = userRepository.findById(user.getUniqueId()).get();

        final RecipeToCook expectedNullRecipe = user.getCookingList().stream().filter(recipeToCook -> recipeToCook.getId().equals(recipeToDelete.getId())).findFirst().orElse(null);

        // Assert
        assertEquals(expectedCookingSize, user.getCookingList().size());
        assertNull(expectedNullRecipe);
    }

    @Test
    public void cookRecipe() {
        // Arrange
        final int recipeToCookIndex = 0;
        final int cookingListSize = user.getCookingList().size();
        final int expectedCookingSize = cookingListSize - 1;

        final RecipeToCook recipeToDelete = user.getCookingList().get(recipeToCookIndex);

        // Act
        cookingListService.cookRecipe(user.getUniqueId(), recipeToDelete.getId());

        user = userRepository.findById(user.getUniqueId()).get();

        final RecipeToCook expectedNullRecipe = user.getCookingList().stream().filter(recipeToCook -> recipeToCook.getId().equals(recipeToDelete.getId())).findFirst().orElse(null);

        // find recipe that we deleted and check transaction
        final Recipe recipeThatHaveTransaction = user.getRecipes().stream().filter(recipe -> recipe.getId().equals(recipeToDelete.getRecipe().getId())).findFirst().get();

        // Assert
        assertEquals(expectedCookingSize, user.getCookingList().size());
        assertNull(expectedNullRecipe);
        assertEquals(LocalDate.now(), recipeThatHaveTransaction.getCookingTransactions().get(0).getCookDate());
    }
}
