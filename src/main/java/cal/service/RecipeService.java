package cal.service;

import cal.model.dto.RecipeDTO;
import cal.model.dto.UserDTO;
import cal.model.entity.Recipe;
import cal.model.entity.User;
import cal.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final Logger LOGGER = Logger.getLogger(RecipeService.class.getName());


    public RecipeService(final UserRepository userRepository){
        this.userRepository = userRepository;
    }

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
            Optional<Recipe> recipeToUpdate = u.getRecipes().stream().filter(recipe -> recipe.getId().equals(recipeDTO.getId())).findFirst();

            recipeToUpdate.ifPresent(recipe -> {
                u.getRecipes().set(u.getRecipes().indexOf(recipe),new Recipe(recipeDTO));
                userRepository.save(u);
                LOGGER.info("RECIPE UPDATED WITH SUCCESS");
            });
        });

        return findAll(userId);
    }

    public List<RecipeDTO> delete(@NotNull UUID userId,@NotNull UUID recipeId){
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u ->{
            Optional<Recipe> recipeToUpdate = u.getRecipes().stream().filter(recipe -> recipe.getId().equals(recipeId)).findFirst();

            recipeToUpdate.ifPresent(recipe -> {
                u.getRecipes().remove(recipe);
                userRepository.save(u);
                LOGGER.info("RECIPE DELETED WITH SUCCESS");
            });
        });

        return findAll(userId);
    }


}
