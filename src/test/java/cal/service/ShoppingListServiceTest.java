package cal.service;


import cal.model.dto.RoutineArticleDTO;
import cal.model.entity.Article;
import cal.model.entity.RoutineArticle;
import cal.model.entity.User;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import cal.repository.UserRepository;
import cal.utility.EntityGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import({ShoppingListService.class,ArticleService.class})
public class ShoppingListServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingListService shoppingListService;

    @Test
    public void addArticlesInShoppingListTest_sameArticlesCase(){
        // Arrange
        User user = userRepository.save(setUpUserWithLogic());

        final int articleAmountToAdd = 4;

        List<RoutineArticleDTO> routineArticles = user.getShoppingList().subList(0,articleAmountToAdd)
                .stream()
                .map(RoutineArticleDTO::new)
                .collect(Collectors.toList());

        // Act
        List<RoutineArticle> newUserShoppingList = shoppingListService.addArticlesInShoppingList(user.getUniqueId(),routineArticles);

        // Assert
        newUserShoppingList.forEach(routineArticle -> {
            Optional<RoutineArticleDTO> routineArticleFromListToAdd = routineArticles
                    .stream()
                    .filter(routineArticleDTO -> routineArticleDTO.getId().equals(routineArticle.getId()))
                    .findFirst();

            routineArticleFromListToAdd.ifPresent(routineArticleDTO -> {
                assertEquals(routineArticle.getQuantity(),routineArticleDTO.getQuantity() * 2);
            });
        });
    }

    @Test
    public void addArticlesInShoppingListTest_differentArticlesCase(){
        // Arrange
        User user = userRepository.save(setUpUserWithLogic());

        final int shoppingListInitialSize = user.getShoppingList().size();
        final int articleAmountToDelete = 2;
        final int expectedShoppingListFinalSize = shoppingListInitialSize - articleAmountToDelete;

        List<RoutineArticleDTO> routineArticles = user.getShoppingList().subList(0,articleAmountToDelete)
                .stream()
                .map(RoutineArticleDTO::new)
                .collect(Collectors.toList());

        // Act
        List<RoutineArticle> newUserShoppingList = shoppingListService.deleteArticlesInShoppingList(user.getUniqueId(),routineArticles);

        // Assert
        assertEquals(expectedShoppingListFinalSize,newUserShoppingList.size());
    }

    @Test
    public void deleteArticlesInShoppingListTest(){
        // Arrange
        User user = userRepository.save(setUpUserWithLogic());

        final int shoppingListInitialiSize = user.getShoppingList().size();
        final int articleAmountToAdd = 2;
        final int expectedShoppingListFinalSize = shoppingListInitialiSize + articleAmountToAdd;

        List<RoutineArticleDTO> routineArticles = EntityGenerator.generateRoutineArticles(articleAmountToAdd)
                .stream()
                .map(RoutineArticleDTO::new)
                .collect(Collectors.toList());

        // Act
        List<RoutineArticle> newUserShoppingList = shoppingListService.addArticlesInShoppingList(user.getUniqueId(),routineArticles);

        // Assert
        assertEquals(expectedShoppingListFinalSize,newUserShoppingList.size());
    }

    @Test
    public void findTest() {
        // Arrange
        User user = userRepository.save(setUpUserWithLogic());

        // Act
        List<RoutineArticle> userShoppingListFromService = shoppingListService.find(user.getUniqueId());

        // Assert
        assertEquals(user.getShoppingList(),userShoppingListFromService);
    }

    @Test
    public void shopTest() {
        // Arrange
        //shop routine then make sure routine is empty
        User user = userRepository.save(setUpUserWithLogic());

        List<RoutineArticleDTO> shoppingList = user.getShoppingList().subList(0,5)
                .stream()
                .map(RoutineArticleDTO::new)
                .collect(Collectors.toList());

        final int finalSize = user.getShoppingList().size() - shoppingList.size();

        // Act
        shoppingListService.shop(user.getUniqueId(),shoppingList);

        user = userRepository.findById(user.getUniqueId()).get();

        // Assert
        assertEquals(finalSize,user.getShoppingList().size());
    }


}
