package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.entity.*;
import cal.model.enums.ArticleCategorie;
import cal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ArticleService articleService;

    private User user;

    @BeforeEach
    public void beforeEach(){
        user = setUpUserWithLogic();
    }

    @Test
    public void createArticleTest() {
        //ARRANGE
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
        int articleIndex = 0;

        ArticleDTO article = new ArticleDTO(user.getArticles().get(articleIndex));

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        //ACT
        article.setArticleCategorie(ArticleCategorie.CEREAL);
        ArticleDTO articleDTO = articleService.update(article, user.getUniqueId());

        //ASSERT
        assertArticle(articleDTO, new Article(article));
    }


    @Test
    public void deleteArticleTest() {
        //ARRANGE
        final int articleIndex = 0;

        Article article = user.getArticles().get(articleIndex);
        article.setImage(UUID.randomUUID().toString());

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        //ACT
        articleService.delete(article.getId(), user.getUniqueId());

        //ASSERT
        assertNull(articleService.find(article.getId(), user.getUniqueId()));
    }

    @Test
    public void findAllOccurencesTest() {
        // Arrange
        User user = setUpUserWithLogic();

        final int occurenceAmount = 6;

        Article articleToFind = user.getArticles().get(0);

        user.getFridge().getAvailableArticles().add(new RoutineArticle(UUID.randomUUID(), articleToFind, 5));

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        // Act
        List<String> allOccurences = articleService.findAllOccurences(user.getUniqueId(), articleToFind.getId());

        // Assert
        assertEquals(occurenceAmount, allOccurences.size());
    }

    @Test
    public void deleteAllOccurencesTest() {
        // Arrange

        User user = setUpUserWithLogic();

        Article articleToDelete = user.getArticles().get(0);

        user.getFridge().getAvailableArticles().add(new RoutineArticle(UUID.randomUUID(), articleToDelete, 5));

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        final int expectedOccurences = 0;
        final int occurenceAmount = articleService.findAllOccurences(user.getUniqueId(), articleToDelete.getId()).size();
        final int initialOccurenceAmount = 6;

        // Act

        articleService.delete(articleToDelete.getId(), user.getUniqueId());

        final int realOccurence = articleService.findAllOccurences(user.getUniqueId(), articleToDelete.getId()).size();

        // Assert

        assertEquals(expectedOccurences, realOccurence);
        assertEquals(initialOccurenceAmount, occurenceAmount);
        assertNotEquals(occurenceAmount, realOccurence);
    }

    @Test
    public void updateAllOccurencesTest() {
        // Arrange

        User user = setUpUserWithLogic();

        final ArticleDTO articleToUpdateDto = new ArticleDTO(user.getArticles().get(0));

        user.getFridge().getAvailableArticles().add(new RoutineArticle(UUID.randomUUID(), new Article(articleToUpdateDto), 5));

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        articleToUpdateDto.setArticleCategorie(ArticleCategorie.CEREAL);

        // Act
        ArticleDTO articleDTO = articleService.update(articleToUpdateDto, user.getUniqueId());

        // Assert
        assertArticle(articleDTO, new Article(articleToUpdateDto));

        // check update in user article list
        Article articleToFind = user.getArticles().stream().filter(article -> article.getId().equals(articleToUpdateDto.getId())).findFirst().get();
        assertArticle(articleToUpdateDto,articleToFind);

        // check in all user routines
        user.getRoutines().forEach(routine -> {
            Optional<RoutineArticle> articleToFindOptional = routine.getRoutineArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleToUpdateDto.getId())).findFirst();

            articleToFindOptional.ifPresent(routineArticle ->{
                assertArticle(articleToUpdateDto,routineArticle.getArticle());
            });
        });

        // check in all shopping list
        RoutineArticle routineArticleToFind = user.getShoppingList().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleToUpdateDto.getId())).findFirst().get();
        assertArticle(articleToUpdateDto, routineArticleToFind.getArticle());

        // check in all recipes list
        user.getRecipes().forEach(recipe -> {
            Optional<RecipeArticle> recipeArticleToFind = recipe.getRecipeArticles().stream().filter(recipeArticle -> recipeArticle.getArticle().getId().equals(articleToUpdateDto.getId())).findFirst();
            recipeArticleToFind.ifPresent(recipeArticle -> assertArticle(articleToUpdateDto,recipeArticle.getArticle()));
        });

        // check in all fridge available recipes
        user.getFridge().getAvailableRecipes().forEach(recipe -> {
            Optional<RecipeArticle> recipeArticleToFind = recipe.getRecipeArticles().stream().filter(recipeArticle -> recipeArticle.getArticle().getId().equals(articleToUpdateDto.getId())).findFirst();
            recipeArticleToFind.ifPresent(recipeArticle -> assertArticle(articleToUpdateDto,recipeArticle.getArticle()));
        });

        // check in all fridge available articles
        routineArticleToFind = user.getFridge().getAvailableArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleToUpdateDto.getId())).findFirst().get();
        assertArticle(articleToUpdateDto, routineArticleToFind.getArticle());

        // check in all fridge missing articles
        routineArticleToFind = user.getFridge().getMissingArticles().stream().filter(routineArticle -> routineArticle.getArticle().getId().equals(articleToUpdateDto.getId())).findFirst().get();
        assertArticle(articleToUpdateDto, routineArticleToFind.getArticle());

    }

    private void assertArticle(ArticleDTO articleDTO, Article article) {
        assertEquals(articleDTO.getArticleCategorie(), article.getArticleCategorie());
        assertEquals(articleDTO.getName(), article.getName());
        assertEquals(articleDTO.getArticleDetail(), article.getArticleDetail());
        assertEquals(articleDTO.getPrice(), article.getPrice());
        assertEquals(articleDTO.getImage(), article.getImage());
        assertEquals(articleDTO.getArticleType(), article.getArticleType());
        assertEquals(articleDTO.getArticleCategorie(), article.getArticleCategorie());
        assertEquals(articleDTO.getTransactions().stream().map(Transaction::new).collect(Collectors.toList()), article.getTransactions());
    }

}
