package cal.service;

import cal.model.dto.FridgeDTO;
import cal.model.dto.RecipeDTO;
import cal.model.entity.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import({FridgeService.class,RecipeService.class})
public class FridgeServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FridgeService fridgeService;

    @Test
    public void findTest(){
        //ARRANGE
        User user = userRepository.save(setUpUser());

        Fridge fridgeToFind = user.getFridge();

        //ACT
        FridgeDTO foundFridge = fridgeService.find(user.getUniqueId());

        //ASSERT
        assertEquals(fridgeToFind,new Fridge(foundFridge));
    }

    @Test
    public void updateByAddingMissingArticle(){
        //ARRANGE
        User user = userRepository.save(setUpUser());

        Fridge fridgeToUpdate = user.getFridge();

        //ACT
        fridgeToUpdate.setMissingArticles(Arrays.asList(new RoutineArticle(UUID.randomUUID(),new Article(UUID.randomUUID(),"","",5.88f,"",ArticleType.LIQUID,ArticleCategorie.DAIRIES),5)).stream().collect(Collectors.toList()));
        FridgeDTO foundFridge = fridgeService.update(user.getUniqueId(),new FridgeDTO(fridgeToUpdate));

        //ASSERT
        assertEquals(fridgeToUpdate,new Fridge(foundFridge));
    }

    @Test
    public void findAllCookable(){
        //ARRANGE
        User user = userRepository.save(setUpUser());

        //add routine article for a recipe into the fridge
        Recipe recipeToFindCookable = user.getRecipes().get(0);

        List<RoutineArticle> newFridgeRoutineArticle = new ArrayList<>();
        recipeToFindCookable.getRecipeArticles().stream().forEach(recipeArticle -> newFridgeRoutineArticle.add(new RoutineArticle(UUID.randomUUID(),recipeArticle.getArticle(),5)));


        user.getFridge().setAvailableArticles(newFridgeRoutineArticle);
        user = userRepository.save(user);

        //ACT
        List<RecipeDTO> cookableRecipes = fridgeService.findAllCookable(user.getUniqueId());

        //ASSERT
        assertEquals(new Recipe(cookableRecipes.get(0)),recipeToFindCookable);
    }

    private User setUpUser() {
        // set up user
        User user = new User(UUID.randomUUID(), "test2@mail.com", "test", "test", "test", "test");

        //set up une recipe list et un shopping list et une liste darticle
        for (int i = 0; i < 10; i++) {
            List<RecipeArticle> recipeArticles = new ArrayList<>();

            for (int j = 0; j < 10; j++) {
                Article article = new Article(UUID.randomUUID(), "test", "test", 5.99f, "test", ArticleType.LIQUID, ArticleCategorie.DAIRIES);
                recipeArticles.add(new RecipeArticle(UUID.randomUUID(),article,"5g", UnitMeasurement.CUP));
                user.getArticles().add(article);
                user.getShoppingList().add(new RoutineArticle(UUID.randomUUID(), article, 5));
            }

            user.getRecipes().add(new Recipe(UUID.randomUUID(),"spaghetti",recipeArticles,"","","", RecipeType.BREAKFAST,5));
        }

        return user;
    }
}
