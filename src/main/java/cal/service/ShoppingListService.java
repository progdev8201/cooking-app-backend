package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.RoutineArticleDTO;
import cal.model.dto.TransactionDTO;
import cal.model.entity.Article;
import cal.model.entity.RoutineArticle;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Validated
@Service
public class ShoppingListService {

    private UserRepository userRepository;

    private ArticleService articleService;

    private final Logger LOGGER = Logger.getLogger(ShoppingListService.class.getName());


    public ShoppingListService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.articleService = new ArticleService(userRepository);
    }

    public List<RoutineArticle> updateShoppingList(@NotNull UUID userId, @NotNull Set<RoutineArticleDTO> shoppingListArticles) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            u.getShoppingList().clear();

            shoppingListArticles.forEach(routineArticleDTO -> {
                u.getShoppingList().add(new RoutineArticle(routineArticleDTO));
            });

            userRepository.save(u);
            LOGGER.info("SHOPPING LIST UPDATED!");
        });

        return user.get().getShoppingList();
    }

    public List<RoutineArticle> find(@NotNull UUID userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.isPresent() ? user.get().getShoppingList() : null;
    }

    public void shop(@NotNull UUID userId) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            u.getShoppingList().stream().forEach(routineArticle -> {
                addTransactionAndUpdate(userId, routineArticle.getArticle(), routineArticle.getQuantity());
            });

            User userSaved = userRepository.save(userRepository.findById(u.getUniqueId()).get());

            userSaved.getFridge().getAvailableArticles().addAll(userSaved.getShoppingList());
            userSaved.getShoppingList().clear();
            userRepository.save(userSaved);

            LOGGER.info("SHOPPING SUCCESS!");
        });
    }

    private void addTransactionAndUpdate(@NotNull UUID userId, @NotNull Article article, @NotNull int qty) {
        ArticleDTO articleToEdit = articleService.find(userId, article.getId());

        for (int i = 0; i < qty; i++)
            articleToEdit.getTransactions().add(new TransactionDTO(UUID.randomUUID(), LocalDate.now(), article.getPrice()));

        articleService.update(articleToEdit, userId);
    }
}
