package cal.service;

import cal.model.dto.RecipeDTO;
import cal.model.entity.Article;
import cal.model.entity.Recipe;
import cal.model.entity.RecipeArticle;
import cal.model.entity.User;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import cal.model.enums.RecipeType;
import cal.model.enums.UnitMeasurement;
import cal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import(RecipeService.class)
public class RecipeServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeService recipeService;

    @Test
    public void createTest(){
        //ARRANGE
        User user = userRepository.save(setUpUser());

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
        final User user = userRepository.save(setUpUser());

        final Recipe recipeToFind = user.getRecipes().get(0);

        //ACT
        RecipeDTO foundRecipe = recipeService.find(user.getUniqueId(),recipeToFind.getId());

        //ASSERT
        assertEquals(new Recipe(foundRecipe),recipeToFind);

    }

    @Test
    public void updateByRemoveArticleTest(){
        //ARRANGE
        User user = userRepository.save(setUpUser());

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
        User user = userRepository.save(setUpUser());

        final int initialSize = user.getRecipes().size();

        //ACT
        recipeService.delete(user.getUniqueId(),user.getRecipes().get(0).getId());
        user = userRepository.findById(user.getUniqueId()).get();

        //ASSERT
        assertEquals(user.getRecipes().size(),initialSize - 1 );
    }

    private User setUpUser() {
        // set up user
        final User user = new User(UUID.randomUUID(), "test2@mail.com", "test", "test", "test", "test");

        //set up une recipe list
        for (int i = 0; i < 10; i++) {
            List<RecipeArticle> recipeArticles = new ArrayList<>();

            for (int j = 0; j < 10; j++) {
                Article article = new Article(UUID.randomUUID(), "test", "test", 5.99f, "test", ArticleType.LIQUID, ArticleCategorie.DAIRIES);
                recipeArticles.add(new RecipeArticle(UUID.randomUUID(),article,"5g", UnitMeasurement.CUP));
            }

            user.getRecipes().add(new Recipe(UUID.randomUUID(),"spaghetti",recipeArticles,"","","",RecipeType.BREAKFAST,5));
        }

        return user;
    }
}
