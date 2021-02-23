package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.entity.Article;
import cal.model.entity.User;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import cal.repository.UserRepository;
import cal.utility.EntityGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    public void before(){
        articleService = new ArticleService(userRepository);
    }

    @Test
    public void createArticleTest() {
        //ARRANGE
        User user = setUpUser();
        int articleIndex = 0;
        ArticleDTO articleDTO = new ArticleDTO(user.getArticles().get(articleIndex));

        //ACT & ASSERT
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any())).then(inv -> {

            User user2 = inv.getArgument(0);
            Article article = user2.getArticles().get(articleIndex);

            assertArticle(articleDTO, article);

            return null;
        });

        articleService.create(articleDTO, user.getUniqueId());
    }


    @Test
    public void findAllTest() {
        //ARRANGE
        User user = setUpUser();
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        //ACT
        List<ArticleDTO> articleDTOList = articleService.findAll(user.getUniqueId());

        //ASSERT
        for (int i = 0; i < articleDTOList.size(); i++) {
            ArticleDTO articleDTO = articleDTOList.get(i);
            Article article = user.getArticles().get(i);

            assertArticle(articleDTO, article);
        }
    }

    @Test
    public void findTest() {
        //ARRANGE
        User user = setUpUser();
        int articleIndex = 0;
        Article article = user.getArticles().get(articleIndex);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        //ACT
        ArticleDTO articleDTO = articleService.find(user.getUniqueId(), article.getId());

        //ASSERT
        assertArticle(articleDTO, article);
    }

    @Test
    public void updateTest() {
        //ARRANGE
        User user = setUpUser();

        int articleIndex = 0;

        Article article = user.getArticles().get(articleIndex);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        //ACT
        article.setArticleCategorie(ArticleCategorie.CEREAL);
        ArticleDTO articleDTO = articleService.update(new ArticleDTO(article), user.getUniqueId());

        //ASSERT
        assertArticle(articleDTO, article);
    }


    @Test
    public void deleteArticleTest() {
        //ARRANGE
        int articleIndex = 0;
        User user = setUpUser();

        Article article = user.getArticles().get(articleIndex);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        //ACT
        articleService.delete(article.getId(), user.getUniqueId());

        //ASSERT
        assertNull(articleService.find(article.getId(), user.getUniqueId()));
    }

    @Test
    public void findAllOccurencesTest(){
        // Arrange
        User user = EntityGenerator.setUpUserWithLogic();

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        // Act
        List<String> allOccurences = articleService.findAllOccurences(user.getUniqueId(),user.getArticles().get(0).getId());

        // Assert
        assertEquals(4,allOccurences.size());
    }

    private void assertArticle(ArticleDTO articleDTO, Article article) {
        assertEquals(articleDTO.getArticleCategorie(), article.getArticleCategorie());
        assertEquals(articleDTO.getName(), article.getName());
        assertEquals(articleDTO.getArticleDetail(), article.getArticleDetail());
        assertEquals(articleDTO.getPrice(), article.getPrice());
        assertEquals(articleDTO.getImage(), article.getImage());
        assertEquals(articleDTO.getArticleType(), article.getArticleType());
        assertEquals(articleDTO.getArticleCategorie(), article.getArticleCategorie());
        assertEquals(articleDTO.getTransactions(), article.getTransactions());
    }

    private User setUpUser() {
        // set up user
        User user = new User(UUID.randomUUID(), "test2@mail.com", "test", "test", "test", "test");

        // set up user article dto list
        for (int i = 0; i < 10; i++) {
            user.getArticles().add(new Article(UUID.randomUUID(), "test", "test", 5.99f, "test", ArticleType.LIQUID, ArticleCategorie.DAIRIES));
        }

        return user;
    }
}
