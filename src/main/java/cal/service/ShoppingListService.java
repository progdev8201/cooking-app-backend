package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.RoutineArticleDTO;
import cal.model.dto.TransactionDTO;
import cal.model.entity.Article;
import cal.model.entity.RoutineArticle;
import cal.model.entity.Transaction;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

                if (alreadyExistingRoutineArticle.isPresent())
                    u.getShoppingList().remove(alreadyExistingRoutineArticle.get());
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

            articlesToShop.stream().forEach(routineArticle -> {

                Optional<RoutineArticle> routineArticleToShop = u.getShoppingList()
                        .stream()
                        .filter(similarRoutineArticle -> similarRoutineArticle.getId().equals(routineArticle.getId()))
                        .findFirst();

                if (routineArticleToShop.isPresent()) {
                    u.getShoppingList().remove(routineArticleToShop.get());

                    Article updatedArticle = addTransactionAndUpdate(u, routineArticleToShop.get().getArticle().getId(), routineArticle.getQuantity());

                    routineArticle.setArticle(new ArticleDTO(updatedArticle));

                    u.getFridge().getAvailableArticles().add(new RoutineArticle(routineArticle));
                }

            });

            userRepository.save(u);

            LOGGER.info("SHOPPING SUCCESS!");
        });
    }

    private Article addTransactionAndUpdate(User user, @NotNull UUID articleId, @NotNull int qty) {
        Article articleToEdit = user.getArticles().stream().filter(article1 -> article1.getId().equals(articleId)).findFirst().get();

        for (int i = 0; i < qty; i++) {
            articleToEdit.getTransactions().add(new Transaction(UUID.randomUUID(), LocalDate.now(), articleToEdit.getPrice()));
        }

        LOGGER.info(qty + " NEW TRANSACTION" + (qty > 1 ? "S" : "") + " ADDED FOR ARTICLE: " + articleToEdit.getName());

        articleService.updateAllOccurences(new ArticleDTO(articleToEdit),user);

        return articleToEdit;
    }
}
