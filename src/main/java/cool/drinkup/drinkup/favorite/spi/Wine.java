package cool.drinkup.drinkup.favorite.spi;

/**
 * Wine SPI接口
 */
public interface Wine {
    Long getId();
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
    String getTagIba();
    String getTagsOthers();
    String getImage();
} 