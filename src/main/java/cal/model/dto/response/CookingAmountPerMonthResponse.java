package cal.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

//todo create inheritance
@Data
@AllArgsConstructor
public class CookingAmountPerMonthResponse {
    private long amount;
    private int month;
}
