package cal.model.entity;

import cal.model.dto.TransactionDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class Transaction implements Serializable {
    private UUID id;
    private LocalDate bougthDate;
    private float articlePrice;

    public Transaction() {

    }

    public Transaction(TransactionDTO transactionDTO) {
        id = transactionDTO.getId() == null ? UUID.randomUUID() : transactionDTO.getId();
        bougthDate = transactionDTO.getBougthDate();
        articlePrice = transactionDTO.getArticlePrice();
    }

    public Transaction(UUID id, LocalDate bougthDate, float articlePrice) {
        this.id = id;
        this.bougthDate = bougthDate;
        this.articlePrice = articlePrice;
    }
}
