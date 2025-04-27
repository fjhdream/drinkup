package cool.drinkup.drinkup.workflow.internal.controller.resp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowBartenderChatResp extends WorkflowUserWineVo {
    @JsonAlias("theme_feature_tag")
    private String themeFeatureTag;
    @JsonAlias("theme_story")
    private String themeStory;
    private String image;
    @JsonAlias("image_prompt")
    private String imagePrompt;
}
