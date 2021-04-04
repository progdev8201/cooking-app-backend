package cal.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RecipeCookTimePerMonthResponse {
    private long amount;
    private int month;
}
