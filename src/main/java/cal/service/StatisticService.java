package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.CookingTransactionDTO;
import cal.model.dto.RecipeDTO;
import cal.model.dto.TransactionDTO;
import cal.model.dto.response.CookingAmountPerMonthResponse;
import cal.model.dto.response.MoneySpendPerMonthResponse;
import cal.model.dto.response.RecipeCookTimePerMonthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RecipeService recipeService;

    private final int AMOUNT_OF_MONTH = 12;


    public List<MoneySpendPerMonthResponse> findMoneySpentPerMonth(UUID userId, int year) {
        // find all articles
        List<ArticleDTO> articles = articleService.findAll(userId);
        List<MoneySpendPerMonthResponse> moneySpendPerMonthResponses = new ArrayList<>();

        // find all transactions for the year
        List<TransactionDTO> transactionsForYear = articles
                .stream()
                .map(articleDTO ->
                        articleDTO.getTransactions()
                                .stream()
                                .filter(transactionDTO -> transactionDTO.getBougthDate().getYear() == year)
                                .findFirst()
                                .orElse(null)
                )
                .collect(Collectors.toList());

        // divide transaction count per month
        for (int i = 1; i <= AMOUNT_OF_MONTH; i++) {
            final int month = i;

            double count = transactionsForYear
                    .stream()
                    .filter(transactionDTO -> transactionDTO != null)
                    .filter(transactionDTO -> transactionDTO.getBougthDate().getMonth().getValue() == month)
                    .mapToDouble(transactionDTO -> transactionDTO.getArticlePrice())
                    .sum();

            moneySpendPerMonthResponses.add(new MoneySpendPerMonthResponse(count, month));
        }

        return null;
    }

    public List<RecipeCookTimePerMonthResponse> findAmountOfTimeARecipeIsCookedPerMonth(UUID userId, UUID recipeId, int year) {
        // find recipe
        RecipeDTO recipe = recipeService.find(userId, recipeId);

        List<RecipeCookTimePerMonthResponse> recipeCookTimePerMonthResponses = new ArrayList<>();

        // find all transactions
        List<CookingTransactionDTO> transactionsForYear = recipe.getCookingTransactions()
                .stream()
                .filter(cookingTransactionDTO -> cookingTransactionDTO.getCookDate().getYear() == year)
                .collect(Collectors.toList());

        // divide transaction count per month
        for (int i = 1; i <= AMOUNT_OF_MONTH; i++) {
            final int month = i;

            long count = transactionsForYear
                    .stream()
                    .filter(transactionDTO -> transactionDTO != null)
                    .filter(transactionDTO -> transactionDTO.getCookDate().getMonth().getValue() == month)
                    .count();

            recipeCookTimePerMonthResponses.add(new RecipeCookTimePerMonthResponse(count, month));
        }

        return recipeCookTimePerMonthResponses;
    }

    public List<CookingAmountPerMonthResponse> findAmountOfTimeUserCookPerMonth(UUID userId, int year) {
        // find all recipes
        List<RecipeDTO> recipes = recipeService.findAll(userId);

        List<CookingAmountPerMonthResponse> cookingAmountPerMonthResponses = new ArrayList<>();

        // find all transactions for the year
        List<List<CookingTransactionDTO>> transactionsForYearMultiple = recipes
                .stream()
                .map(recipeDTO -> recipeDTO.getCookingTransactions()
                        .stream()
                        .filter(cookingTransactionDTO -> cookingTransactionDTO.getCookDate().getYear() == year)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        List<CookingTransactionDTO> transactionsForYear = new ArrayList<>();

        transactionsForYearMultiple.forEach(cookingTransactionDTOS -> transactionsForYear.addAll(cookingTransactionDTOS));

        // divide transaction count per month
        for (int i = 1; i <= AMOUNT_OF_MONTH; i++) {
            final int month = i;

            long count = transactionsForYear
                    .stream()
                    .filter(transactionDTO -> transactionDTO.getCookDate().getMonth().getValue() == month)
                    .count();

            cookingAmountPerMonthResponses.add(new CookingAmountPerMonthResponse(count, month));
        }

        return cookingAmountPerMonthResponses;
    }
}
