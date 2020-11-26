package cal.model.dto;

import cal.model.entity.Article;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class ArticleDTO implements Serializable {
    private UUID id;
    private String name;
    private String articleDetail;
    private float price;
    private String image;
    private ArticleType articleType;
    private ArticleCategorie articleCategorie;
    private List<TransactionDTO> transactions;

    public ArticleDTO(){

    }

    public ArticleDTO(Article article) {
        id = article.getId();
        name = article.getName();
        articleDetail = article.getArticleDetail();
        price = article.getPrice();
        image = article.getImage();
        articleType = article.getArticleType();
        articleCategorie = article.getArticleCategorie();

        //map transactions to dto

        transactions = article.getTransactions().stream().map(TransactionDTO::new).collect(Collectors.toList());
    }

    public ArticleDTO(String name, String articleDetail, float price, String image, ArticleType articleType, ArticleCategorie articleCategorie) {
        this.name = name;
        this.articleDetail = articleDetail;
        this.price = price;
        this.image = image;
        this.articleType = articleType;
        this.articleCategorie = articleCategorie;
        transactions = new ArrayList<>();
    }
}
