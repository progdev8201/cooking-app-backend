package cal.service;

import cal.model.entity.Article;
import cal.model.entity.Recipe;
import cal.model.entity.RecipeArticle;
import cal.model.entity.User;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import cal.model.enums.RecipeType;
import cal.model.enums.UnitMeasurement;
import cal.repository.ImageRepository;
import cal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class ImageServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;


    @Test
    public void uploadImage_withArticle() throws IOException {
        //ARRANGE
        User user = userRepository.save(setUpUser());

        MockMultipartFile file = new MockMultipartFile("file", "img.png", "multipart/form-data", "salut".getBytes());

        ImageService imageService = new ImageService(imageRepository, userRepository);

        //ACT
        imageService.uploadImage(file, user.getUniqueId(), user.getArticles().get(0).getId(), true);

        user = userRepository.findById(user.getUniqueId()).get();

        ResponseEntity<Resource> fileByte = imageService.download(UUID.fromString(user.getArticles().get(0).getImage()));

        //ASSERT
        assertEquals(fileByte.getBody(), new ByteArrayResource(file.getBytes()));
    }

    @Test
    public void uploadImage_withRecipe() throws IOException {
        //ARRANGE
        User user = userRepository.save(setUpUser());

        MockMultipartFile file = new MockMultipartFile("file", "img.png", "multipart/form-data", "salut".getBytes());

        ImageService imageService = new ImageService(imageRepository, userRepository);

        //ACT
        imageService.uploadImage(file, user.getUniqueId(), user.getRecipes().get(0).getId(), false);

        user = userRepository.findById(user.getUniqueId()).get();

        ResponseEntity<Resource> fileByte = imageService.download(UUID.fromString(user.getRecipes().get(0).getImage()));

        //ASSERT
        assertEquals(fileByte.getBody(), new ByteArrayResource(file.getBytes()));
    }

    @Test
    public void uploadImage_withRecipe_withBadUUIDFormat() throws IOException {
        //ARRANGE
        User user = setUpUser();
        user.getRecipes().get(0).setImage("unvalid uuid");
        user = userRepository.save(user);

        MockMultipartFile file = new MockMultipartFile("file", "img.png", "multipart/form-data", "salut".getBytes());

        ImageService imageService = new ImageService(imageRepository, userRepository);


        //ACT
        imageService.uploadImage(file, user.getUniqueId(), user.getRecipes().get(0).getId(), false);

        user = userRepository.findById(user.getUniqueId()).get();

        ResponseEntity<Resource> fileByte = imageService.download(UUID.fromString(user.getRecipes().get(0).getImage()));

        //ASSERT
        assertEquals(fileByte.getBody(), new ByteArrayResource(file.getBytes()));
    }


    private User setUpUser() {
        // set up user
        User user = new User(UUID.randomUUID(), "test2@mail.com", "test", "test", "test", "test");
        List<RecipeArticle> recipeArticles = new ArrayList<>();

        // set up user article dto list
        for (int i = 0; i < 10; i++) {
            Article article = new Article(UUID.randomUUID(), "test", "test", 5.99f, UUID.randomUUID().toString(), ArticleType.LIQUID, ArticleCategorie.DAIRIES);
            user.getArticles().add(article);
            recipeArticles.add(new RecipeArticle(UUID.randomUUID(), article, "5g", UnitMeasurement.CUP));
        }

        user.getRecipes().add(new Recipe(UUID.randomUUID(), "spaghetti", recipeArticles, UUID.randomUUID().toString(), "", "", RecipeType.BREAKFAST, 5));

        return user;
    }

}
