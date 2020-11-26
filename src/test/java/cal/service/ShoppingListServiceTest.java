package cal.service;


import cal.model.dto.RoutineArticleDTO;
import cal.model.entity.Article;
import cal.model.entity.RoutineArticle;
import cal.model.entity.User;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import cal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class ShoppingListServiceTest {

    @Autowired
    private UserRepository userRepository;
    private final Logger LOGGER = Logger.getLogger(ShoppingListServiceTest.class.getName());

    @Test
    public void updateByDeleteShoppingListTest() {
        //ARRANGE

        //add routine articles to shopping list
        User user = userRepository.save(setUpUser());

        ShoppingListService shoppingListService = new ShoppingListService(userRepository);

        int initialSize = user.getShoppingList().size();

        RoutineArticleDTO routineArticleDTOToDelete = null;

        //map shopping list to dto
        Set<RoutineArticleDTO> shoppingList = new TreeSet<>();

        user.getShoppingList().stream().forEach(routineArticle -> {
            shoppingList.add(new RoutineArticleDTO(routineArticle));
        });

        //ACT

        // then update by deleting one item in the list
        for (RoutineArticleDTO routineArticle : shoppingList) {
            routineArticleDTOToDelete = routineArticle;
            break;
        }

        shoppingList.remove(routineArticleDTOToDelete);

        shoppingListService.updateShoppingList(user.getUniqueId(), shoppingList);

        user = userRepository.findById(user.getUniqueId()).get();

        //ASSERT
        // then verify if size has changed
        assertTrue(initialSize - 1 == user.getShoppingList().size());
    }

    @Test
    public void findTest() {
        //ARRANGE
        //make sure to receive a user shopping list
        User user = userRepository.save(setUpUser());
        ShoppingListService shoppingListService = new ShoppingListService(userRepository);

        //ACT
        Set<RoutineArticle> userShoppingListFromService = shoppingListService.find(user.getUniqueId());

        //ASSERT
        assertTrue(user.getShoppingList().equals(userShoppingListFromService));
    }

    @Test
    public void shopTest() {
        //ARRANGE
        //shop routine then make sure routine is empty
        User user = userRepository.save(setUpUser());
        ShoppingListService shoppingListService = new ShoppingListService(userRepository);
        int initialSize = user.getShoppingList().size();

        //ACT
        shoppingListService.shop(user.getUniqueId());
        user = userRepository.findById(user.getUniqueId()).get();

        //ASSERT
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
