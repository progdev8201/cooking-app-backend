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
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import(ShoppingListService.class)
public class ShoppingListServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingListService shoppingListService;

    @Test
    public void updateByDeleteShoppingListTest() {
        // Arrange

        //add routine articles to shopping list
        User user = userRepository.save(setUpUser());

        int initialSize = user.getShoppingList().size();

        RoutineArticleDTO routineArticleDTOToDelete = null;

        //map shopping list to dto
        Set<RoutineArticleDTO> shoppingList = new TreeSet<>();

        user.getShoppingList().stream().forEach(routineArticle -> {
            shoppingList.add(new RoutineArticleDTO(routineArticle));
        });

        // Act

        // then update by deleting one item in the list
        for (RoutineArticleDTO routineArticle : shoppingList) {
            routineArticleDTOToDelete = routineArticle;
            break;
        }

        shoppingList.remove(routineArticleDTOToDelete);

        shoppingListService.updateShoppingList(user.getUniqueId(), shoppingList);

        user = userRepository.findById(user.getUniqueId()).get();

        // Assert
        // then verify if size has changed
        assertTrue(initialSize - 1 == user.getShoppingList().size());
    }

    @Test
    public void addArticlesToShoppingListTest_sameArticlesCase(){
        // Arrange
        User user = userRepository.save(setUpUserWithLogic());

        final int articleAmountToAdd = 4;

        List<RoutineArticleDTO> routineArticles = user.getShoppingList().subList(0,articleAmountToAdd)
                .stream()
                .map(RoutineArticleDTO::new)
                .collect(Collectors.toList());

        // Act
        List<RoutineArticle> newUserShoppingList = shoppingListService.addArticlesToShoppingList(user.getUniqueId(),routineArticles);

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
    public void addArticlesToShoppingListTest_differentArticlesCase(){
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
        List<RoutineArticle> newUserShoppingList = shoppingListService.addArticlesToShoppingList(user.getUniqueId(),routineArticles);

        // Assert
        assertEquals(expectedShoppingListFinalSize,newUserShoppingList.size());
    }

    @Test
    public void findTest() {
        // Arrange
        User user = userRepository.save(setUpUser());

        // Act
        List<RoutineArticle> userShoppingListFromService = shoppingListService.find(user.getUniqueId());

        // Assert
        assertTrue(user.getShoppingList().equals(userShoppingListFromService));
    }

    @Test
    public void shopTest() {
        // Arrange
        //shop routine then make sure routine is empty
        User user = userRepository.save(setUpUser());
        ShoppingListService shoppingListService = new ShoppingListService(userRepository);
        int initialSize = user.getShoppingList().size();

        // Act
        shoppingListService.shop(user.getUniqueId());
        user = userRepository.findById(user.getUniqueId()).get();

        // Assert
        assertTrue(initialSize > 0 && user.getShoppingList().isEmpty());
    }

    private User setUpUser() {
        // set up user
        User user = new User(UUID.randomUUID(), "test2@mail.com", "test", "test", "test", "test");

        //set up une shopping list
        for (int i = 0; i < 10; i++) {
            Article article = new Article(UUID.randomUUID(), "test" + i, "test", 5.99f, "test", ArticleType.LIQUID, ArticleCategorie.DAIRIES);
            user.getArticles().add(article);
            user.getShoppingList().add(new RoutineArticle(UUID.randomUUID(), article, i));
        }

        return user;
    }
}
