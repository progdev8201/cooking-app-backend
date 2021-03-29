package cal.service;

import cal.model.dto.FridgeDTO;
import cal.model.dto.RecipeDTO;
import cal.model.entity.*;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import cal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import({FridgeService.class,RecipeService.class})
public class FridgeServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FridgeService fridgeService;

    private User user;

    @BeforeEach
    public void beforeEach(){
        user = userRepository.save(setUpUserWithLogic());
    }

    @Test
    public void findTest(){
        //ARRANGE
        Fridge fridgeToFind = user.getFridge();

        //ACT
        FridgeDTO foundFridge = fridgeService.find(user.getUniqueId());

        //ASSERT
        assertEquals(fridgeToFind,new Fridge(foundFridge));
    }

    @Test
    public void updateByAddingMissingArticle(){
        //ARRANGE
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

   }
