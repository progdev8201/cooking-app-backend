package cal.model.dto;

import cal.model.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class UserDTO implements Serializable {
    private UUID uniqueId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private List<RecipeDTO> recipes;
    private List<ArticleDTO> articles;
    private List<RoutineDTO> routines;
    private List<RoutineArticleDTO> shoppingList;
    private List<RecipeToCookDTO> cookingList;
    private FridgeDTO frigo;

    public UserDTO(User user) {
        uniqueId = user.getUniqueId();
        email = user.getEmail();
        password = user.getPassword();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        role = user.getRole();
        frigo = new FridgeDTO(user.getFridge());

        //map lists
        recipes = user.getRecipes().stream().map(RecipeDTO::new).collect(Collectors.toList());

        articles = user.getArticles().stream().map(ArticleDTO::new).collect(Collectors.toList());

        routines = user.getRoutines().stream().map(RoutineDTO::new).collect(Collectors.toList());

        shoppingList = user.getShoppingList().stream().map(RoutineArticleDTO::new).collect(Collectors.toList());

        cookingList = user.getCookingList().stream().map(RecipeToCookDTO::new).collect(Collectors.toList());
    }
}
