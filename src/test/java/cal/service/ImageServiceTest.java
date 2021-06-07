package cal.service;

import cal.model.entity.User;
import cal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.UUID;

import static cal.utility.EntityGenerator.setUpUserWithLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@Import({ImageService.class, RecipeService.class, ArticleService.class})
public class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void beforeEach() {
        user = userRepository.save(setUpUserWithLogic());
    }

    @Test
    public void uploadAndDownloadImage_withArticle() throws IOException {
        //ARRANGE
        MockMultipartFile file = new MockMultipartFile("file", "img.png", "multipart/form-data", "salut".getBytes());

        //ACT
        imageService.uploadImage(file, user.getUniqueId(), user.getArticles().get(0).getId(), true);

        user = userRepository.findById(user.getUniqueId()).get();

        ResponseEntity<Resource> fileByte = imageService.download(UUID.fromString(user.getArticles().get(0).getImage()));

        //ASSERT
        assertEquals(fileByte.getBody(), new ByteArrayResource(file.getBytes()));
    }

    @Test
    public void uploadAndDownloadImage_withRecipe() throws IOException {
        //ARRANGE
        MockMultipartFile file = new MockMultipartFile("file", "img.png", "multipart/form-data", "salut".getBytes());

        //ACT
        imageService.uploadImage(file, user.getUniqueId(), user.getRecipes().get(0).getId(), false);

        user = userRepository.findById(user.getUniqueId()).get();

        ResponseEntity<Resource> fileByte = imageService.download(UUID.fromString(user.getRecipes().get(0).getImage()));

        //ASSERT
        assertEquals(fileByte.getBody(), new ByteArrayResource(file.getBytes()));
    }

    @Test
    public void uploadAndDownloadImage_withRecipe_withBadUUIDFormat() throws IOException {
        //ARRANGE
        user.getRecipes().get(0).setImage("invalid uuid");

        user = userRepository.save(user);

        MockMultipartFile file = new MockMultipartFile("file", "img.png", "multipart/form-data", "salut".getBytes());

        //ACT
        imageService.uploadImage(file, user.getUniqueId(), user.getRecipes().get(0).getId(), false);

        user = userRepository.findById(user.getUniqueId()).get();

        ResponseEntity<Resource> fileByte = imageService.download(UUID.fromString(user.getRecipes().get(0).getImage()));

        //ASSERT
        assertEquals(fileByte.getBody(), new ByteArrayResource(file.getBytes()));
    }

    @Test
    public void uploadAndDownloadImage_withRecipe_withNullReturned() throws IOException {
        //ARRANGE
        user.getRecipes().get(0).setImage("invalid uuid");

        user = userRepository.save(user);

        MockMultipartFile file = new MockMultipartFile("file", "img.png", "multipart/form-data", "salut".getBytes());

        //ACT
        imageService.uploadImage(file, user.getUniqueId(), user.getRecipes().get(0).getId(), false);

        user = userRepository.findById(user.getUniqueId()).get();

        imageService.deleteImage(user.getRecipes().get(0).getImage());

        ResponseEntity<Resource> fileByte = imageService.download(UUID.fromString(user.getRecipes().get(0).getImage()));

        //ASSERT
        assertNull(fileByte);
    }

}
