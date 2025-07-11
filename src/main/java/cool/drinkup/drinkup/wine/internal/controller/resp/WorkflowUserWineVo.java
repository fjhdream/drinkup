package cool.drinkup.drinkup.wine.internal.controller.resp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cool.drinkup.drinkup.favorite.spi.FavoriteType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowUserWineVo implements cool.drinkup.drinkup.shared.dto.UserWine {
    private String id;
    @JsonAlias("user_id")
    private String userId;
    private String name;
    @JsonAlias("name_en")
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
    @JsonAlias("tag_base_spirits")
    @JsonDeserialize(using = StringOrListDeserializer.class)
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
    @JsonAlias("theme_story")
    private String themeStory;
    @JsonAlias("theme_feature_tag")
    private String themeFeatureTag;
    @JsonAlias("create_date")
    private String createDate;
    @JsonAlias("update_date")
    private String updateDate;
    private Integer favoriteCount;
    private FavoriteType favoriteType;

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

    public static class StringOrListDeserializer extends JsonDeserializer<List<String>> {
        @Override
        public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            List<String> result = new ArrayList<>();
            
            if (node.isTextual()) {
                result.add(node.asText());
            } else if (node.isArray()) {
                node.forEach(element -> result.add(element.asText()));
            }
            
            return result;
        }
    }
} 