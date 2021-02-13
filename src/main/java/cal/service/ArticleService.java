package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.TransactionDTO;
import cal.model.dto.UserDTO;
import cal.model.entity.Article;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Service
@Validated
public class ArticleService {

    private UserRepository userRepository;

    private final Logger LOGGER = Logger.getLogger(ArticleService.class.getName());

    public ArticleService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public ArticleDTO create(@Valid ArticleDTO articleDTO,UUID userId){
        Optional<User> user = userRepository.findById(userId);
        Article article = new Article(articleDTO);

        if (user.isPresent()){
            user.get().getArticles().add(article);
            userRepository.save(user.get());
            LOGGER.info("NEW ARTICLE CREATED");
        }

        return find(userId,article.getId());
    }

    public List<ArticleDTO> findAll(UUID userId){
        Optional<UserDTO> userDTO = userRepository.findById(userId).stream().map(UserDTO::new).findFirst();

        return userDTO.isPresent() ? userDTO.get().getArticles() : null;
    }

    public ArticleDTO find(UUID userId,UUID articleId){
        return findAll(userId).stream().filter(articleDTO ->articleDTO.getId().equals(articleId)).findFirst().orElse(null);
    }

    public ArticleDTO update(@Valid ArticleDTO articleDTO,UUID userId){
        // find user then update his article then save the client then return the article
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u ->{
            Optional<Article> articleToUpdate = u.getArticles().stream().filter(article -> article.getId().equals(articleDTO.getId())).findFirst();

            articleToUpdate.ifPresent(article -> {
                u.getArticles().set(u.getArticles().indexOf(article),new Article(articleDTO));
                userRepository.save(u);
                LOGGER.info("ARTICLE UPDATED");
            });
        });

        return find(userId,articleDTO.getId());
    }

    public void delete(UUID articleId,UUID userId){
        // find user then find article list then delete article then save user
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u ->{
            Optional<Article> articleToDelete = u.getArticles().stream().filter(article -> article.getId().equals(articleId)).findFirst();

            articleToDelete.ifPresent(article -> {
                u.getArticles().remove(article);
                userRepository.save(u);
                LOGGER.info("NEW ARTICLE DELETED");
            });
        });
    }

}
