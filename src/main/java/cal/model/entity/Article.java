package cal.model.entity;

import cal.model.dto.ArticleDTO;
import cal.model.dto.TransactionDTO;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Article implements Serializable {
    private UUID id;
    private String name;
    private String articleDetail;
    private float price;
    private String image;
    private ArticleType articleType;
    private ArticleCategorie articleCategorie;
    private List<Transaction> transactions;

    public Article(){
    }

    public Article(ArticleDTO articleDTO) {
        this.id = articleDTO.getId() == null ? UUID.randomUUID() : articleDTO.getId();
        this.name = articleDTO.getName();
        this.articleDetail = articleDTO.getArticleDetail();
        this.price = articleDTO.getPrice();
        this.image = articleDTO.getImage();
        this.articleType = articleDTO.getArticleType();
        this.articleCategorie = articleDTO.getArticleCategorie();
        transactions = new ArrayList<>();

        //map transactions from dto
        articleDTO.getTransactions().stream().forEach(transaction ->transactions.add(new Transaction(transaction)));

    }

    public Article(UUID id,String name, String articleDetail, float price, String image, ArticleType articleType, ArticleCategorie articleCategorie) {
        this.id = id;
        this.name = name;
        this.articleDetail = articleDetail;
        this.price = price;
        this.image = image;
        this.articleType = articleType;
        this.articleCategorie = articleCategorie;
        this.transactions = new ArrayList<>();
    }
}
