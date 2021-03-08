package cal.service.validator;

import cal.model.dto.ArticleDTO;
import cal.model.entity.Article;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

public class ArticleServiceStaticValidator {

    public static void uniqueArticleNameValidator(List<Article> articles, ArticleDTO articleDTO, UUID userId){
        if (articleDTO.getId() == null)
            handleCreationValidation(articleDTO, articles);
        else
            handleUpdateValidation(articleDTO, articles);

    }

    private static void handleUpdateValidation(ArticleDTO articleDTO, List<Article> articles) {
        //  check if name has changed
        boolean hasNameNotChanged = articles.stream().filter(article -> article.getName().equals(articleDTO.getName()) && article.getId().equals(articleDTO.getId())).findFirst().isPresent();

        if (hasNameNotChanged)
            return;

        boolean isNameExistant = articles.stream().filter(article -> article.getName().equals(articleDTO.getName())).findFirst().isPresent();

        if (isNameExistant)
            throwArticleRepetitionException();
    }

    private static void handleCreationValidation(ArticleDTO articleDTO, List<Article> articles) {
        boolean isNamePresent = articles.stream().filter(article -> article.getName().equals(articleDTO.getName())).findFirst().isPresent();

        if (isNamePresent)
            throwArticleRepetitionException();
    }

    private static void throwArticleRepetitionException(){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name of Article Cannot Be repeated", null);
    }
}
