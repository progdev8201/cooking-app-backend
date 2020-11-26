package cal.controller;

import cal.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @PostMapping("/{userId}/{imageObjectId}/{isArticle}")
    public void uploadImage(@RequestParam("file") MultipartFile file, @PathVariable final UUID userId, @PathVariable final UUID imageObjectId,@PathVariable final boolean isArticle) throws IOException {
        imageService.uploadImage(file,userId,imageObjectId,isArticle);
    }

    @GetMapping("/download/{imageId}")
    public ResponseEntity<Resource> download(@PathVariable final UUID imageId) {
        return imageService.download(imageId);
    }

    @GetMapping("/download/string")
    public String hello() {
        return "Hello world";
    }
}
