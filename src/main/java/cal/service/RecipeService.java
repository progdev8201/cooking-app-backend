package cal.service;

import cal.model.dto.RecipeDTO;
import cal.model.dto.UserDTO;
import cal.model.entity.Recipe;
import cal.model.entity.RecipeToCook;
import cal.model.entity.User;
import cal.repository.ImageRepository;
import cal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@Validated
public class RecipeService {

    @Autowired
    private  UserRepository userRepository;

    @Lazy
    @Autowired
    private  ImageService imageService;

    private final Logger LOGGER = Logger.getLogger(RecipeService.class.getName());

    // SERVICES

    public RecipeDTO create(@Valid RecipeDTO recipeDTO,@NotNull UUID userId){
        Optional<User> user = userRepository.findById(userId);
        Recipe recipe = new Recipe(recipeDTO);

        if (user.isPresent()){
            user.get().getRecipes().add(recipe);
            userRepository.save(user.get());
            LOGGER.info("RECIPE CREATED WITH SUCCESS");
        }

        return find(userId,recipe.getId());
    }

    public RecipeDTO find(@NotNull UUID userId,@NotNull UUID recipeId){
        Optional<User> user = userRepository.findById(userId);

        return user.isPresent() ? new UserDTO(user.get()).getRecipes().stream().filter(recipeDTO -> recipeDTO.getId().equals(recipeId)).findFirst().get() : null;
    }

    public List<RecipeDTO> findAll(@NotNull UUID userId){
        Optional<User> user = userRepository.findById(userId);

        return user.isPresent() ? new UserDTO(user.get()).getRecipes() : null;
    }

    public List<RecipeDTO> update(@NotNull UUID userId,@Valid RecipeDTO recipeDTO){
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u ->{
            // update recipe in cooking list
            Optional<RecipeToCook> recipeToCook = u.getCookingList().stream().filter(recipeToCook1 -> recipeToCook1.getRecipe().getId().equals(recipeDTO.getId())).findFirst();

            // todo get real recipe and map dto values into it
            recipeToCook.ifPresent(recipeToCook1 -> {
                recipeToCook1.setRecipe(new Recipe(recipeDTO));
                LOGGER.info("RECIPE WITH ID: "+ recipeDTO.getId() + " WAS UPDATED IN COOKING LIST WITH SUCCESS");
            });

            // update recipe in recipe list
            Optional<Recipe> recipeToUpdate = u.getRecipes().stream().filter(recipe -> recipe.getId().equals(recipeDTO.getId())).findFirst();

            recipeToUpdate.ifPresent(recipe -> {
                // todo when adding mapping method no need to index of
                u.getRecipes().set(u.getRecipes().indexOf(recipe),new Recipe(recipeDTO));
                userRepository.save(u);
                LOGGER.info("RECIPE WITH ID: "+ recipeDTO.getId() + " WAS UPDATED WITH SUCCESS");
            });
        });

        // todo pas besoin de refetch dans la bd pour return cela
        return findAll(userId);
    }

    public List<RecipeDTO> delete(@NotNull UUID userId,@NotNull UUID recipeId){
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u ->{
            //todo clean code

            // delete recipe in cooking list
            Optional<RecipeToCook> recipeToCook = u.getCookingList()
                    .stream()
                    .filter(recipeToCook1 -> recipeToCook1.getRecipe().getId().equals(recipeId))
                    .findFirst();

            recipeToCook.ifPresent(recipeToCook1 -> u.getCookingList().remove(recipeToCook1));

            // delete recipe in user recipe list + delete image
            Optional<Recipe> recipeToDelete = u.getRecipes().stream().filter(recipe -> recipe.getId().equals(recipeId)).findFirst();

            recipeToDelete.ifPresent(recipe -> {
                // should remove recipe everywhere
                u.getRecipes().remove(recipe);

                if (recipe.getImage() != null)
                    imageService.deleteImage(recipe.getImage());

                userRepository.save(u);

                LOGGER.info("RECIPE DELETED WITH SUCCESS");
            });
        });

        // todo pas besoin de refetch dans la bd pour return cela
        return findAll(userId);
    }


}
