package cool.drinkup.drinkup.favorite.spi;

import java.time.ZonedDateTime;

/**
 * UserWine SPI接口
 */
public interface UserWine {
    Long getId();
    Long getUserId();
    String getName();
    String getNameEn();
    String getDescription();
    String getBaseIngredient();
    String getIngredients();
    String getIngredientOverview();
    String getIngredientAlcoholOverview();
    String getMethod();
    String getGarnish();
    String getGlassware();
    String getFlavor();
    String getMixology();
    String getPreparationTime();
    String getAlcoholContent();
    String getCalories();
    String getInvented();
    String getTagMainBaseSpirit();
    String getTagBaseSpirit();
    String getTagGlass();
    String getTagMixology();
    String getTagCocktailType();
    String getTagFlavor();
    String getTagComplexity();
    String getTagAbv();
    String getTagsOthers();
    String getImage();
    String getThemeStory();
    String getThemeFeatureTag();
    ZonedDateTime getCreateDate();
    ZonedDateTime getUpdateDate();
} 