package cal.service.validator;

import cal.model.dto.ArticleDTO;
import cal.model.entity.Article;
import cal.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArticleServiceStaticValidator {


    public static void uniqueArticleNameValidator(ArticleService articleService, ArticleDTO articleDTO, UUID userId){
        List<Article> articles = articleService.findAll(userId)
                .stream()
                .map(Article::new)
                .collect(Collectors.toList());

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name of Article Cannot Be repeated", null);
    }

    private static void handleCreationValidation(ArticleDTO articleDTO, List<Article> articles) {
        boolean isNamePresent = articles.stream().filter(article -> article.getName().equals(articleDTO.getName())).findFirst().isPresent();

        if (isNamePresent)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name of Article Cannot Be repeated", null);
    }
}
