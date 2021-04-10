package cal.service;

import cal.model.dto.RecipeDTO;
import cal.model.dto.response.RecipeStringResponse;
import cal.model.entity.Recipe;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import({RecipeService.class,ImageService.class})
public class RecipeServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeService recipeService;

    private User user;

    @BeforeEach
    public void beforeEach(){
        user = userRepository.save(setUpUserWithLogic());
    }

    @Test
    public void createTest(){
        //ARRANGE
        final int initialSize = user.getRecipes().size();

        final RecipeDTO recipeToAdd = new RecipeDTO(user.getRecipes().get(0));
        recipeToAdd.setId(UUID.randomUUID());

        //ACT
        recipeService.create(recipeToAdd,user.getUniqueId());
        user = userRepository.findById(user.getUniqueId()).get();

        //ASSERT
        assertEquals(user.getRecipes().size(),initialSize + 1);
    }

    @Test
    public void findTest(){
        //ARRANGE
        final Recipe recipeToFind = user.getRecipes().get(0);

        //ACT
        RecipeDTO foundRecipe = recipeService.find(user.getUniqueId(),recipeToFind.getId());

        //ASSERT
        assertEquals(new Recipe(foundRecipe),recipeToFind);

    }

    @Test
    public void updateByRemoveArticleTest(){
        //ARRANGE
        final int indexToGet = 0;
        final Recipe recipeToUpdate = user.getCookingList().get(indexToGet).getRecipe();

        final int initialSize = recipeToUpdate.getRecipeArticles().size();
        final int expectedSize = initialSize - 1;


        recipeToUpdate.getRecipeArticles().remove(indexToGet);

        //ACT
        recipeService.update(user.getUniqueId(),new RecipeDTO(recipeToUpdate));

        user = userRepository.findById(user.getUniqueId()).get();

        //ASSERT
        assertEquals(user.getRecipes().get(indexToGet).getRecipeArticles().size(),expectedSize);
        assertEquals(user.getCookingList().get(indexToGet).getRecipe().getRecipeArticles().size(),expectedSize);
    }

    @Test
    public void findAllRecipeStrings(){
        // Assert

        // Act
        List<RecipeStringResponse> recipeStringResponses = recipeService.findAllRecipeStrings(user.getUniqueId());
        List<Recipe> recipes = user.getRecipes();

        // Arrange
        recipeStringResponses.forEach(recipeStringResponse -> {
            assertTrue(recipes.stream().anyMatch(recipe -> recipe.getId().equals(recipeStringResponse.getRecipeId())));
            assertTrue(recipes.stream().anyMatch(recipe -> recipe.getName().equals(recipeStringResponse.getRecipeName())));
        });
    }

    @Test
    public void deleteTest(){
        //ARRANGE
        final int initialSize = user.getRecipes().size();
        final int initialRecipeToCookSize = user.getCookingList().size();
        final int finalExepectedRecipeToCookSize = initialRecipeToCookSize - 1;

        //ACT
        recipeService.delete(user.getUniqueId(),user.getRecipes().get(0).getId());

        user = userRepository.findById(user.getUniqueId()).get();

        //ASSERT
        assertEquals(user.getRecipes().size(),initialSize - 1 );
        assertEquals(finalExepectedRecipeToCookSize,user.getCookingList().size());
    }
}
