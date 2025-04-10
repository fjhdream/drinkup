package cool.drinkup.drinkup.workflow.controller.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowBartenderChatResp extends WorkflowUserWineVo{
    private String themeFeatureTag;
    private String themeStory;
}
