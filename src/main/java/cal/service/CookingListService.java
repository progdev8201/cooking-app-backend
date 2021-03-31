package cal.service;

import cal.model.dto.RecipeDTO;
import cal.model.dto.RecipeToCookDTO;
import cal.model.entity.Recipe;
import cal.model.entity.RecipeToCook;
import cal.model.entity.User;
import cal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CookingListService {

    @Autowired
    private UserRepository userRepository;

    private static Logger LOGGER = Logger.getLogger(CookingListService.class.getName());

    public List<RecipeToCookDTO> findAll(UUID userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.isPresent() ? user.get().getCookingList().stream().map(RecipeToCookDTO::new).collect(Collectors.toList()) : null;
    }

    public void addRecipesToList(UUID userId, List<RecipeDTO> recipes, LocalDate cookDate) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {
            recipes.forEach(r -> {

                UUID recipeId = UUID.randomUUID();

                u.getCookingList().add(new RecipeToCook(recipeId, new Recipe(r), cookDate));

                LOGGER.info("RECIPE WITH ID: " + recipeId + " AND WITH NAME: " + r.getName() + " WAS ADDED SUCCESSFULLY");

            });

            userRepository.save(u);
        });
    }

    public RecipeToCookDTO updateCookDay(UUID userId, LocalDate cookDate, UUID recipeToCookId) {
        Optional<User> user = userRepository.findById(userId);

        RecipeToCook recipeToCookToUpdate = null;

        if (user.isPresent()) {

            User u = user.get();

            Optional<RecipeToCook> recipeToCookToUpdateOpt = u.getCookingList()
                    .stream()
                    .filter(recipeToCook -> recipeToCook.getId().equals(recipeToCookId))
                    .findFirst();

            if (recipeToCookToUpdateOpt.isPresent()) {

                recipeToCookToUpdate = recipeToCookToUpdateOpt.get();

                int indexToModify = u.getCookingList().indexOf(recipeToCookToUpdate);

                // todo debug to see if the recipe to cook is updated in the list without the set
                recipeToCookToUpdate.setCookDate(cookDate);

                u.getCookingList().set(indexToModify, recipeToCookToUpdate);

                userRepository.save(u);

                LOGGER.info("LOCAL DATE OF RECIPE WITH ID: " + recipeToCookToUpdate.getId() + " AND WITH NAME: " + recipeToCookToUpdate.getRecipe().getName() + " WAS UPDATED SUCCESSFULLY");

            }
        }

        return recipeToCookToUpdate == null ? null : new RecipeToCookDTO(recipeToCookToUpdate);
    }

    public void deleteRecipes(UUID userId, List<UUID> recipesToDelete) {
        Optional<User> user = userRepository.findById(userId);

        user.ifPresent(u -> {

            recipesToDelete.forEach(recipeToCookDTOId -> {

                Optional<RecipeToCook> recipeToCookToDelete = u.getCookingList()
                        .stream()
                        .filter(recipeToCook -> recipeToCook.getId().equals(recipeToCookDTOId))
                        .findFirst();

                if (recipeToCookToDelete.isPresent()) {

                    RecipeToCook r = recipeToCookToDelete.get();

                    u.getCookingList().remove(r);

                    LOGGER.info("RECIPE TO COOK WITH ID: " + r.getId() + "WAS DELETED");
                }
            });

            userRepository.save(u);

        });
    }

}
