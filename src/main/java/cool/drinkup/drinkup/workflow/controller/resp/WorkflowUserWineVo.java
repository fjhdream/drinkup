package cool.drinkup.drinkup.workflow.controller.resp;

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
    public static class Ingredient {
        private String ingredientName;
        private String ingredientIconType;
        private String ml;
        private String oz;
        private String cl;
    }
}
