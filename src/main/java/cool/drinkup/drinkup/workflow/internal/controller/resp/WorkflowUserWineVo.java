package cool.drinkup.drinkup.workflow.internal.controller.resp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowUserWineVo {
    private String id;
    private String name;
    private String nameEn;
    private String description;
    @JsonAlias("base_ingredient")
    private String baseIngredient;
    private List<Ingredient> ingredients;
    @JsonAlias("ingredient_overview")
    private String ingredientOverview;
    @JsonAlias("ingredient_alcohol_overview")
    private String ingredientAlcoholOverview;
    private String method;
    private String garnish;
    private String glassware;
    private String flavor;
    private String mixology;
    @JsonAlias("preparation_time")
    private String preparationTime;
    @JsonAlias("alcohol_content")
    private String alcoholContent;
    private String calories;
    private String invented;
    @JsonAlias("tag_main_base_spirit")
    private String tagMainBaseSpirit;
    @JsonAlias("tag_base_spirit")
    private List<String> tagBaseSpirit;
    @JsonAlias("tag_glass")
    private String tagGlass;
    @JsonAlias("tag_mixology")
    private String tagMixology;
    @JsonAlias("tag_cocktail_type")
    private String tagCocktailType;
    @JsonAlias("tag_flavor")
    private List<String> tagFlavor;
    @JsonAlias("tag_complexity")
    private String tagComplexity;
    @JsonAlias("tag_abv")
    private String tagAbv;
    @JsonAlias("tags_others")
    private List<String> tagsOthers;
    private String image;
    @JsonAlias("create_date")
    private String createDate;
    @JsonAlias("update_date")
    private String updateDate;

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
