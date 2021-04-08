package cal.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoneySpentPerMonthResponse {
    private double amount;
    private int month;
}
