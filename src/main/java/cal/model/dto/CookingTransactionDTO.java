package cal.model.dto;

import cal.model.entity.CookingTransaction;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CookingTransactionDTO {
    private UUID id;
    private LocalDate cookDate;

    public CookingTransactionDTO() {
    }

    public CookingTransactionDTO(CookingTransaction cookingTransaction) {
        id = cookingTransaction.getId();
        cookDate = cookingTransaction.getCookDate();
    }
}
