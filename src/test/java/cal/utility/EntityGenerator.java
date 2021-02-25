package cal.utility;

import cal.model.entity.*;
import cal.model.enums.ArticleCategorie;
import cal.model.enums.ArticleType;
import cal.model.enums.RecipeType;
import cal.model.enums.UnitMeasurement;

import java.util.*;
import java.util.stream.Collectors;

public class EntityGenerator {
    public static final int USER_ARTICLE_SIZE = 10;
    public static final int USER_ROUTINE_SIZE = 10;
    public static final int USER_RECIPE_SIZE = 10;
    public static final int USER_M_SIZE = 10;

    public static User setUpCompleteUser(){
        User user = new User(UUID.randomUUID(),"test","test","test","test","test");

        // create articles
        user.setArticles(generateArticles(USER_ARTICLE_SIZE));
        // create routine
        user.setRoutines(generateRoutines(USER_ROUTINE_SIZE,USER_ROUTINE_SIZE));
        // create recipe
        user.setRecipes(generateRecipes(USER_RECIPE_SIZE,USER_RECIPE_SIZE));
        // create fridge
        user.setFridge(generateFridge(10,10,10));

        return user;
    }

    //setting up a user with all entity based on real logique
    public static User setUpUserWithLogic(){
        User user = new User(UUID.randomUUID(),"test","test","test","test","test");

        // create 20 articles
        user.setArticles(generateArticles(20));

        // fill up 2 routine
        List<RoutineArticle> routineArticlesPart1 = user.getArticles().subList(0,user.getArticles().size()/2)
                .stream()
                .map(article -> new RoutineArticle(UUID.randomUUID(),article,5))
                .collect(Collectors.toList());

        List<RoutineArticle> routineArticlesPart2 = user.getArticles().subList(user.getArticles().size()/2,user.getArticles().size())
                .stream()
                .map(article -> new RoutineArticle(UUID.randomUUID(),article,5))
                .collect(Collectors.toList());

        Routine routine = new Routine(UUID.randomUUID(),"test",routineArticlesPart1);
        Routine routine2 = new Routine(UUID.randomUUID(),"test2",routineArticlesPart2);

        // create 2 recipes
        List<RecipeArticle> recipeArticlesPart1 = user.getArticles().subList(0,user.getArticles().size()/2)
                .stream()
                .map(article -> new RecipeArticle(UUID.randomUUID(),article,"test",UnitMeasurement.CUP))
                .collect(Collectors.toList());

        List<RecipeArticle> recipeArticlesPart2 = user.getArticles().subList(user.getArticles().size()/2,user.getArticles().size() - 1)
                .stream()
                .map(article -> new RecipeArticle(UUID.randomUUID(),article,"test",UnitMeasurement.CUP))
                .collect(Collectors.toList());

        Recipe recipe = new Recipe(UUID.randomUUID(),"test",recipeArticlesPart1,"test","test","test", RecipeType.BREAKFAST,5);
        Recipe recipe2 = new Recipe(UUID.randomUUID(),"test",recipeArticlesPart2,"test","test","test", RecipeType.BREAKFAST,5);

        // create a fridge
        Fridge fridge = new Fridge();
        fridge.setMissingArticles(new ArrayList<>(routineArticlesPart1));
        fridge.setAvailableArticles(new ArrayList<>(routineArticlesPart2));
        fridge.setAvailableRecipes(Arrays.asList(new Recipe(recipe.getId(),recipe.getName(),new ArrayList<>(recipe.getRecipeArticles()),recipe.getImage(),recipe.getCountry(),recipe.getDescription(),recipe.getRecipeType(),recipe.getTime())));

        user.setRecipes(Arrays.asList(recipe,recipe2));
        user.setRoutines(Arrays.asList(routine,routine2));
        user.setFridge(fridge);
        user.setShoppingList(new ArrayList<>(routineArticlesPart1));

        return user;
    }

    // todo might have to delete all of those methods if not used
    public static Article generateArticle(){
        return new Article(UUID.randomUUID(),"test", "test", 5.99f, "test", ArticleType.SOLID,ArticleCategorie.CEREAL);
    }

    public static RoutineArticle generateRoutineArticle(){
        return new RoutineArticle(UUID.randomUUID(), generateArticle(),5);
    }

    public static RecipeArticle generateRecipeArticle(){
        return new RecipeArticle(UUID.randomUUID(), generateArticle(),"test", UnitMeasurement.CUP);
    }

    public static List<RoutineArticle> generateRoutineArticles(int size){
        List<RoutineArticle> routineArticles = new ArrayList<>();

        for (int i = 0; i < size; i++)
            routineArticles.add(generateRoutineArticle());

        return routineArticles;
    }

    public static List<RecipeArticle> generateRecipeArticles(int size){
        List<RecipeArticle> recipeArticles = new ArrayList<>();

        for (int i = 0; i < size; i++)
            recipeArticles.add(generateRecipeArticle());

        return recipeArticles;
    }

    public static List<Article> generateArticles(int size){
        List<Article> articles = new ArrayList<>();

        for (int i = 0; i < size; i++)
            articles.add(generateArticle());

        return articles;
    }

    public static Recipe generateRecipe(int recipeArticlesAmount){
        return new Recipe(UUID.randomUUID(),"test",generateRecipeArticles(recipeArticlesAmount),"test","test","test", RecipeType.BREAKFAST,5);
    }

    public static List<Recipe> generateRecipes(int size,int recipeArticleAmountPerRecipe){
        List<Recipe> recipes = new ArrayList<>();

        for (int i = 0; i < size; i++)
            recipes.add(generateRecipe(recipeArticleAmountPerRecipe));

        return recipes;
    }

    public static Routine generateRoutine(int routineArticlesAmount){
        return new Routine(UUID.randomUUID(),"test",generateRoutineArticles(routineArticlesAmount));
    }

    public static List<Routine> generateRoutines(int size,int routineArticleAmountPerRoutine){
        List<Routine> routines = new ArrayList<>();

        for (int i = 0; i < size; i++)
            routines.add(generateRoutine(routineArticleAmountPerRoutine));

        return routines;
    }

    public static Fridge generateFridge(int availableRecipeAmount, int availableArticlesAmount, int missingArticleAmount){
        Fridge fridge = new Fridge();

        // find highest size in order to loop only once instead of multiple times

        int highestSize = 0;

        if (availableArticlesAmount > availableRecipeAmount && availableArticlesAmount > missingArticleAmount){
            highestSize = availableArticlesAmount;
        }else if (availableRecipeAmount > availableArticlesAmount && availableRecipeAmount > missingArticleAmount){
            highestSize = availableRecipeAmount;
        }else highestSize = missingArticleAmount;

        //fill up fridge

        for (int i = 0; i < highestSize; i++) {
            if (i < availableArticlesAmount)
                fridge.getAvailableArticles().add(generateRoutineArticle());

            if (i < availableRecipeAmount)
                fridge.getAvailableRecipes().add(generateRecipe(5));

            if (i < missingArticleAmount)
                fridge.getMissingArticles().add(generateRoutineArticle());
        }

        return fridge;
    }

}
