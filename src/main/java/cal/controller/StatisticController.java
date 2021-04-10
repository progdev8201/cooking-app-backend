package cal.controller;

import cal.model.dto.response.AllStatisticsResponse;
import cal.model.dto.response.CookingAmountPerMonthResponse;
import cal.model.dto.response.MoneySpentPerMonthResponse;
import cal.model.dto.response.RecipeCookTimePerMonthResponse;
import cal.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/statistic")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("moneySpentPerMonth/{userId}/{year}")
    public List<MoneySpentPerMonthResponse> findMoneySpentPerMonth(@PathVariable final UUID userId, @PathVariable final int year) {
        return statisticService.findMoneySpentPerMonth(userId,year);
    }

    @GetMapping("timeRecipeCookedPerMonth/{userId}/{recipeId}/{year}")
    public List<RecipeCookTimePerMonthResponse> findAmountOfTimeARecipeIsCookedPerMonth(@PathVariable final UUID userId,@PathVariable final UUID recipeId, @PathVariable final int year) {
        return statisticService.findAmountOfTimeARecipeIsCookedPerMonth(userId,recipeId,year);
    }

    @GetMapping("timeUserCookPerMonth/{userId}/{year}")
    public List<CookingAmountPerMonthResponse> findAmountOfTimeUserCookPerMonth(@PathVariable final UUID userId, @PathVariable final int year) {
        return statisticService.findAmountOfTimeUserCookPerMonth(userId,year);
    }

    @GetMapping("allStats/{userId}/{year}")
    public AllStatisticsResponse findAllStatistics(@PathVariable final UUID userId,@PathVariable final int year){
        return statisticService.findAllStatistics(userId, year);
    }
}
