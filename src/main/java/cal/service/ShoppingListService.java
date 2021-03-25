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

    private final UserRepository userRepository;

    private final ArticleService articleService;

    private final Logger LOGGER = Logger.getLogger(ShoppingListService.class.getName());


    public ShoppingListService(final UserRepository userRepository) {
        this.userRepository = userRepository;
        this.articleService = new ArticleService(userRepository);
    }

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


        return userRepository.save(user.get()).getShoppingList();
    }

    public List<RoutineArticle> deleteArticlesInShoppingList(@NotNull UUID userId, @NotNull List<RoutineArticleDTO> articlesToDelete) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {

            articlesToDelete.forEach(routineArticle -> {

                Optional<RoutineArticle> alreadyExistingRoutineArticle = u.getShoppingList()
                        .stream()
                        .filter(routineArticle1 -> routineArticle1.getId().equals(routineArticle.getId()))
                        .findFirst();

                if (alreadyExistingRoutineArticle.isPresent()) {
                    final int routineArticleIndex = u.getShoppingList().indexOf(alreadyExistingRoutineArticle.get());

                    u.getShoppingList().remove(routineArticleIndex);
                }
            });

            LOGGER.info("ARTICLES DELETED IN SHOPPING LIST!");
        });

        return userRepository.save(user.get()).getShoppingList();
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

        for (int i = 0; i < qty; i++) {
            articleToEdit.getTransactions().add(new TransactionDTO(UUID.randomUUID(), LocalDate.now(), article.getPrice()));
        }

        LOGGER.info(qty + " NEW TRANSACTION" + (qty > 1 ? "S" : "") + " ADDED FOR ARTICLE: " + articleToEdit.getName());

        articleService.update(articleToEdit, userId);
    }
}
