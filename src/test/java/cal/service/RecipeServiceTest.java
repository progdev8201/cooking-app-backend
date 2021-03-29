package cal.service;

import cal.model.dto.RecipeDTO;
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

import java.util.UUID;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        final Recipe recipeToUpdate = user.getRecipes().get(0);

        final int initialSize = recipeToUpdate.getRecipeArticles().size();

        recipeToUpdate.getRecipeArticles().remove(0);

        //ACT
        recipeService.update(user.getUniqueId(),new RecipeDTO(recipeToUpdate));

        user = userRepository.findById(user.getUniqueId()).get();

        //ASSERT
        assertEquals(user.getRecipes().get(0).getRecipeArticles().size(),initialSize - 1);

    }

    @Test
    public void deleteTest(){
        //ARRANGE
        final int initialSize = user.getRecipes().size();

        //ACT
        recipeService.delete(user.getUniqueId(),user.getRecipes().get(0).getId());
        user = userRepository.findById(user.getUniqueId()).get();

        //ASSERT
        assertEquals(user.getRecipes().size(),initialSize - 1 );
    }
}
