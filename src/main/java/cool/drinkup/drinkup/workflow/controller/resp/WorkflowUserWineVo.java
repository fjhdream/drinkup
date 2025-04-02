package cool.drinkup.drinkup.workflow.controller.resp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import lombok.Data;

@Data
public class WorkflowUserWineVo {
    private String name;
    private String description;
    private String baseIngredient;
    private List<Ingredient> ingredients;
    private String ingredientOverview;
    private String ingredientAlcoholOverview;
    private String method;
    private String garnish;
    private String glassware;
    private String flavor;
    private String mixology;
    private String preparationTime;
    private String alcoholContent;
    private String calories;
    private String invented;
    private String tagMainBaseSpirit;
    private List<String> tagBaseSpirit;
    private String tagGlass;
    private String tagMixology;
    private String tagCocktailType;
    private String tagFlavor;
    private String tagComplexity;
    private String tagAbv;
    private List<String> tagsOthers;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Ingredient {
        @JsonAlias("ingredient_name")
        private String ingredientName;
        @JsonAlias("ingredient_icon_type")
        private String ingredientIconType;
        @JsonAlias("ml")
        private String ml;
        @JsonAlias("oz")
        private String oz;
        @JsonAlias("cl")
        private String cl;
    }
}
