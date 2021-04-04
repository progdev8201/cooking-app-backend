package cal.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoneySpendPerMonthResponse {
    private double amount;
    private int month;
}
