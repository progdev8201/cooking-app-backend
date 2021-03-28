package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.RoutineArticleDTO;
import cal.model.entity.Article;
import cal.model.entity.RoutineArticle;
import cal.model.entity.Transaction;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Validated
@Service
public class ShoppingListService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleService articleService;

    private final Logger LOGGER = Logger.getLogger(ShoppingListService.class.getName());

    // SERVICES

    public List<RoutineArticle> addArticlesInShoppingList(@NotNull UUID userId, @NotNull List<RoutineArticleDTO> articlesToAdd) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {

            articlesToAdd.forEach(routineArticle -> {

                Optional<RoutineArticle> alreadyExistingRoutineArticle = u.getShoppingList()
                        .stream()
                        .filter(routineArticle1 -> routineArticle1.getId().equals(routineArticle.getId()))
                        .findFirst();

                if (alreadyExistingRoutineArticle.isPresent()) {
                    final int routineArticleIndex = u.getShoppingList().indexOf(alreadyExistingRoutineArticle.get());

                    final int quantityToAdd = alreadyExistingRoutineArticle.get().getQuantity() + routineArticle.getQuantity();

                    u.getShoppingList().get(routineArticleIndex).setQuantity(quantityToAdd);
                } else {
                    u.getShoppingList().add(new RoutineArticle(routineArticle));
                }
            });

            LOGGER.info("ARTICLES ADDED TO SHOPPING LIST!");
        });

        return user.isPresent() ? userRepository.save(user.get()).getShoppingList() : null;
    }

    public List<RoutineArticle> deleteArticlesInShoppingList(@NotNull UUID userId, @NotNull List<RoutineArticleDTO> articlesToDelete) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {

            articlesToDelete.forEach(routineArticle -> {

                Optional<RoutineArticle> alreadyExistingRoutineArticle = u.getShoppingList()
                        .stream()
                        .filter(routineArticle1 -> routineArticle1.getId().equals(routineArticle.getId()))
                        .findFirst();

                alreadyExistingRoutineArticle.ifPresent(u.getShoppingList()::remove);
            });

            LOGGER.info("ARTICLES DELETED IN SHOPPING LIST!");
        });

        return user.isPresent() ? userRepository.save(user.get()).getShoppingList() : null;
    }

    public List<RoutineArticle> find(@NotNull UUID userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.isPresent() ? user.get().getShoppingList() : null;
    }

    public void shop(@NotNull UUID userId, List<RoutineArticleDTO> articlesToShop) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {

            articlesToShop.forEach(routineArticle -> {

                Optional<RoutineArticle> routineArticleToShop = u.getShoppingList()
                        .stream()
                        .filter(similarRoutineArticle -> similarRoutineArticle.getArticle().getId().equals(routineArticle.getArticle().getId()))
                        .findFirst();

                if (routineArticleToShop.isPresent()) {
                    u.getShoppingList().remove(routineArticleToShop.get());

                    Optional<Article> updatedArticle = addTransactionAndUpdate(u, routineArticleToShop.get().getArticle().getId(), routineArticle.getQuantity());

                    updatedArticle.ifPresent(article -> {
                        routineArticle.setArticle(new ArticleDTO(article));

                        u.getFridge().getAvailableArticles().add(new RoutineArticle(routineArticle));
                    });
                }

            });

            userRepository.save(u);

            LOGGER.info("SHOPPING SUCCESS!");
        });
    }

    // PRIVATE METHODS

    private Optional<Article> addTransactionAndUpdate(User user, @NotNull UUID articleId, @NotNull int qty) {
        Optional<Article> articleToEdit = user.getArticles().stream().filter(article1 -> article1.getId().equals(articleId)).findFirst();

        if (articleToEdit.isPresent()) {

            for (int i = 0; i < qty; i++) {
                articleToEdit.get().getTransactions().add(new Transaction(UUID.randomUUID(), LocalDate.now(), articleToEdit.get().getPrice()));
            }

            LOGGER.info(qty + " NEW TRANSACTION" + (qty > 1 ? "S" : "") + " ADDED FOR ARTICLE: " + articleToEdit.get().getName());

            articleService.updateAllOccurences(new ArticleDTO(articleToEdit.get()), user);

        }

        return articleToEdit;
    }
}
