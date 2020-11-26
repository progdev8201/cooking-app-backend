package cal.model.dto;

import cal.model.entity.Transaction;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionDTO implements Serializable {
    private UUID id;
    private LocalDate bougthDate;
    private float articlePrice;

    public TransactionDTO(){

    }

    public TransactionDTO(UUID id, LocalDate bougthDate, float articlePrice) {
        this.id = id;
        this.bougthDate = bougthDate;
        this.articlePrice = articlePrice;
    }

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.bougthDate = transaction.getBougthDate();
        this.articlePrice = transaction.getArticlePrice();
    }
}
