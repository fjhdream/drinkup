package cool.drinkup.drinkup.workflow.controller.resp;

import java.util.List;

import lombok.Data;

@Data
public class WorkflowBartenderChatResp {
    private CocktailInfo cocktailInfo;

    @Data
    public static class CocktailInfo {
        private String englishName;
        private String chineseName;
        private String description;
        private Information information;
        private String glassType;
        private List<Ingredient> ingredients;
        private List<String> instructions;
        private String tips;
        private List<String> tags;
        private String visualSymbol;
        private String drinkingGuide;
        private String photographySuggestion;
        private String coverDesign;
        private String drinkChallenge;
    }

    @Data
    public static class Information {
        private String baseSpirit;
        private String alcohol;
        private String calories;
        private String duration;
    }

    @Data
    public static class Ingredient {
        private String name;
        private String amount;
        private String unit;
        private String replacement;
    }
}
