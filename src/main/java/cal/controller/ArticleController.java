package cal.controller;

import cal.model.dto.ArticleDTO;
import cal.model.entity.Article;
import cal.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/{userId}")
    public ArticleDTO create(@Valid @RequestBody final ArticleDTO articleDTO,@PathVariable final UUID userId){
        return articleService.create(articleDTO,userId);
    }

    @GetMapping("/{articleId}/{userId}")
    public ArticleDTO find(@PathVariable final UUID articleId,@PathVariable final UUID userId){
        return articleService.find(articleId,userId);
    }

    @GetMapping("/{userId}")
    public List<ArticleDTO> findAll(@PathVariable final UUID userId){
        return articleService.findAll(userId);
    }

    @GetMapping("/findAllOccurences/{userId}/{articleId}")
    public List<String> findAllOccurences(@PathVariable final UUID userId,@PathVariable final UUID articleId){
        return articleService.findAllOccurences(userId,articleId);
    }

    @PutMapping("/{userId}")
    public ArticleDTO update(@Valid @RequestBody ArticleDTO articleDTO, @PathVariable final UUID userId){
        return articleService.update(articleDTO,userId);
    }

    @DeleteMapping("/{articleId}/{userId}")
    public void delete(@PathVariable final UUID articleId,@PathVariable final UUID userId){
        articleService.delete(articleId,userId);
    }

}
