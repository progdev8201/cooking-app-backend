package cal.service;

import cal.model.dto.ArticleDTO;
import cal.model.dto.RecipeDTO;
import cal.model.entity.Image;
import cal.repository.ImageRepository;
import cal.repository.UserRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ImageService {

    private ImageRepository imageRepository;
    private UserRepository userRepository;
    private RecipeService recipeService;
    private ArticleService articleService;
    private final Logger LOGGER = Logger.getLogger(ImageService.class.getName());

    // CONSTRUCTORS

    public ImageService(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        recipeService = new RecipeService(this.userRepository);
        articleService = new ArticleService(this.userRepository);
    }


    // SERVICES

    public void uploadImage(@NotNull MultipartFile file, @NotNull UUID userId, @NotNull UUID imageObjectId, @NotNull boolean isArticle) throws IOException {
        Image image = imageRepository.save(new Image(UUID.randomUUID(), StringUtils.cleanPath(file.getOriginalFilename()), file.getContentType(), file.getBytes()));

        if (isArticle) attributeImageToArticle(userId, imageObjectId, image);
        else attributeImageToRecipe(userId, imageObjectId, image);

        LOGGER.info("IMAGE CREATION SUCCESS");
    }

    public ResponseEntity<Resource> download(UUID imageId) {
        Image image = imageRepository.findById(imageId).get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(new ByteArrayResource(image.getData()));
    }


    // PRIVATE METHODS

    private void attributeImageToRecipe(UUID userId, UUID imageObjectId, Image image) {
        RecipeDTO recipe = recipeService.find(userId, imageObjectId);

        if (isUUID(recipe.getImage()))
            imageRepository.deleteById(UUID.fromString(recipe.getImage()));

        recipe.setImage(image.getId().toString());

        recipeService.update(userId, recipe);
    }

    private void attributeImageToArticle(UUID userId, UUID imageObjectId, Image image) {
        ArticleDTO article = articleService.find(userId, imageObjectId);

        if (isUUID(article.getImage()))
            imageRepository.deleteById(UUID.fromString(article.getImage()));

        article.setImage(image.getId().toString());

        articleService.update(article, userId);
    }

    private boolean isUUID(String id){
        try{
            UUID uuid = UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException exception){
            return false;
        }
    }
}
