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

import java.time.LocalDate;
import java.util.Arrays;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void beforeEach(){
        user = userRepository.save(setUpUserWithLogic());
    }

    @Test
    public void addRecipesToListTest(){
        // Arrange
        final int initialSize = user.getCookingList().size();

        final int finalSize = initialSize + 1;

        final RecipeDTO recipeToAdd = new RecipeDTO(user.getRecipes().get(0));

        // Act
        cookingListService.addRecipesToList(user.getUniqueId(), Arrays.asList(recipeToAdd), LocalDate.now());

        user = userRepository.findById(user.getUniqueId()).get();

        // Assert
        assertEquals(finalSize, user.getCookingList().size());
        assertEquals(new Recipe(recipeToAdd),user.getCookingList().get(finalSize - 1).getRecipe());
    }

    public void updateCookDayTest(){
        // Arrange

        // Act

        // Assert
    }

    public void deleteRecipesTest(){
        // Arrange

        // Act

        // Assert
    }
}
