package cal.model.entity;

import cal.model.dto.CookingTransactionDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CookingTransaction implements Serializable {
    private UUID id;
    private LocalDate cookDate;

    public CookingTransaction(){

    }

    public CookingTransaction(UUID id, LocalDate cookDate) {
        this.id = id;
        this.cookDate = cookDate;
    }

    public CookingTransaction(CookingTransactionDTO cookingTransactionDTO){
        id = cookingTransactionDTO.getId() == null ? UUID.randomUUID() : cookingTransactionDTO.getId();
        cookDate = cookingTransactionDTO.getCookDate();

    }

}
