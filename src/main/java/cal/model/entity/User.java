package cal.model.entity;

import cal.model.dto.RoutineArticleDTO;
import cal.model.dto.UserDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Document
public class User implements Serializable {

    @Id
    private UUID uniqueId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private List<Recipe> recipes;
    private List<Article> articles;
    private List<Routine> routines;
    private List<RoutineArticle> shoppingList;
    private Fridge fridge;

    public User(UUID uniqueId, String email, String password, String firstName, String lastName, String role) {
        this.uniqueId = uniqueId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        recipes = new ArrayList<>();
        articles = new ArrayList<>();
        routines = new ArrayList<>();
        shoppingList = new ArrayList<>();
        fridge = new Fridge();
    }

    public User(UserDTO userDTO) {
        uniqueId = userDTO.getUniqueId();
        email = userDTO.getEmail();
        password = userDTO.getPassword();
        firstName = userDTO.getFirstName();
        lastName = userDTO.getLastName();
        role = userDTO.getRole();
        fridge = new Fridge(userDTO.getFrigo());

        // map lists
        recipes = userDTO.getRecipes().stream().map(Recipe::new).collect(Collectors.toList());

        articles = userDTO.getArticles().stream().map(Article::new).collect(Collectors.toList());

        routines = userDTO.getRoutines().stream().map(Routine::new).collect(Collectors.toList());

        shoppingList = userDTO.getShoppingList().stream().map(RoutineArticle::new).collect(Collectors.toList());
    }

    public User() {

    }
}
