package cal.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllStatisticsResponse {
    private double averageMoneySpentPerMonth;
    private double moneySpentThisYear;
    private double averageTimeCookPerMonth;
    private List<CookingAmountPerMonthResponse> cookingAmountPerMonthResponses;
    private List<MoneySpentPerMonthResponse> moneySpentPerMonthResponses;
}