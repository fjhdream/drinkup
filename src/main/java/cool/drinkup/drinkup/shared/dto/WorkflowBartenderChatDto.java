package cool.drinkup.drinkup.shared.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowBartenderChatDto extends WorkflowWineVo {
    @JsonAlias("theme_feature_tag")
    private String themeFeatureTag;
    @JsonAlias("theme_story")
    private String themeStory;
    private String image;
    @JsonAlias("image_prompt")
    private String imagePrompt;
} 