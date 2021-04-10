package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.CookingTransactionDTO;
import cal.model.dto.RecipeDTO;
import cal.model.dto.TransactionDTO;
import cal.model.dto.response.AllStatisticsResponse;
import cal.model.dto.response.CookingAmountPerMonthResponse;
import cal.model.dto.response.MoneySpentPerMonthResponse;
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

    public List<MoneySpentPerMonthResponse> findMoneySpentPerMonth(final UUID userId, final int year) {
        // find all articles
        final List<ArticleDTO> articles = articleService.findAll(userId);
        final List<MoneySpentPerMonthResponse> moneySpentPerMonthRespons = new ArrayList<>();

        // find all transactions for the year
        final List<List<TransactionDTO>> transactionsForYearMultiple = articles
                .stream()
                .map(articleDTO ->
                        articleDTO.getTransactions()
                                .stream()
                                .filter(transactionDTO -> transactionDTO.getBougthDate().getYear() == year)
                                .collect(Collectors.toList())
                )
                .collect(Collectors.toList());

        final List<TransactionDTO> transactionsForYear = new ArrayList<>();

        transactionsForYearMultiple.forEach(transactionsForYear::addAll);

        // divide transaction count per month
        for (int i = 1; i <= AMOUNT_OF_MONTH; i++) {
            final int month = i;

            final double count = transactionsForYear
                    .stream()
                    .filter(transactionDTO -> transactionDTO != null)
                    .filter(transactionDTO -> transactionDTO.getBougthDate().getMonth().getValue() == month)
                    .mapToDouble(transactionDTO -> transactionDTO.getArticlePrice())
                    .sum();

            moneySpentPerMonthRespons.add(new MoneySpentPerMonthResponse(count, month));
        }

        return moneySpentPerMonthRespons;
    }

    public List<RecipeCookTimePerMonthResponse> findAmountOfTimeARecipeIsCookedPerMonth(final UUID userId, final UUID recipeId, final int year) {
        // find recipe
        final RecipeDTO recipe = recipeService.find(userId, recipeId);

        final List<RecipeCookTimePerMonthResponse> recipeCookTimePerMonthResponses = new ArrayList<>();

        // find all transactions
        final List<CookingTransactionDTO> transactionsForYear = recipe.getCookingTransactions()
                .stream()
                .filter(cookingTransactionDTO -> cookingTransactionDTO.getCookDate().getYear() == year)
                .collect(Collectors.toList());

        // divide transaction count per month
        for (int i = 1; i <= AMOUNT_OF_MONTH; i++) {
            final int month = i;

            final long count = transactionsForYear
                    .stream()
                    .filter(transactionDTO -> transactionDTO != null)
                    .filter(transactionDTO -> transactionDTO.getCookDate().getMonth().getValue() == month)
                    .count();

            recipeCookTimePerMonthResponses.add(new RecipeCookTimePerMonthResponse(count, month));
        }

        return recipeCookTimePerMonthResponses;
    }

    public List<CookingAmountPerMonthResponse> findAmountOfTimeUserCookPerMonth(final UUID userId, final int year) {
        // find all recipes
        final List<RecipeDTO> recipes = recipeService.findAll(userId);

        final List<CookingAmountPerMonthResponse> cookingAmountPerMonthResponses = new ArrayList<>();

        // find all transactions for the year
        final List<List<CookingTransactionDTO>> transactionsForYearMultiple = recipes
                .stream()
                .map(recipeDTO -> recipeDTO.getCookingTransactions()
                        .stream()
                        .filter(cookingTransactionDTO -> cookingTransactionDTO.getCookDate().getYear() == year)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        final List<CookingTransactionDTO> transactionsForYear = new ArrayList<>();

        transactionsForYearMultiple.forEach(cookingTransactionDTOS -> transactionsForYear.addAll(cookingTransactionDTOS));

        // divide transaction count per month
        for (int i = 1; i <= AMOUNT_OF_MONTH; i++) {
            final int month = i;

            final long count = transactionsForYear
                    .stream()
                    .filter(transactionDTO -> transactionDTO.getCookDate().getMonth().getValue() == month)
                    .count();

            cookingAmountPerMonthResponses.add(new CookingAmountPerMonthResponse(count, month));
        }

        return cookingAmountPerMonthResponses;
    }

    public AllStatisticsResponse findAllStatistics(final UUID userId, final int year){
        List<MoneySpentPerMonthResponse> moneySpentPerMonthRespons = findMoneySpentPerMonth(userId, year);
        List<CookingAmountPerMonthResponse> cookingAmountPerMonthResponses = findAmountOfTimeUserCookPerMonth(userId, year);

        double averageMoneySpentPerMonth = findAverageMoneySpentPerMonth(moneySpentPerMonthRespons);
        double averageTimeCookPerMonth = findAverageTimeCookPerMonth(cookingAmountPerMonthResponses);
        double moneySpentThisYear = findMoneySpentThisYear(moneySpentPerMonthRespons);

        return new AllStatisticsResponse(averageMoneySpentPerMonth,moneySpentThisYear,averageTimeCookPerMonth,cookingAmountPerMonthResponses, moneySpentPerMonthRespons);
    }

    // PRIVATE METHODS

    private double findAverageMoneySpentPerMonth(List<MoneySpentPerMonthResponse> moneySpentPerMonthRespons){
        return moneySpentPerMonthRespons
                .stream()
                .mapToDouble(MoneySpentPerMonthResponse::getAmount)
                .average()
                .orElse(Double.NaN);
    }

    private double findAverageTimeCookPerMonth(List<CookingAmountPerMonthResponse> cookingAmountPerMonthResponses){
        return cookingAmountPerMonthResponses
                .stream()
                .mapToDouble(CookingAmountPerMonthResponse::getAmount)
                .average()
                .orElse(Double.NaN);
    }

    private double findMoneySpentThisYear(List<MoneySpentPerMonthResponse> moneySpentPerMonthRespons){
        return moneySpentPerMonthRespons
                .stream()
                .mapToDouble(MoneySpentPerMonthResponse::getAmount)
                .sum();
    }
}