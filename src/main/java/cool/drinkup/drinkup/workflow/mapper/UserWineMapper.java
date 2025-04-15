package cool.drinkup.drinkup.workflow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import cool.drinkup.drinkup.workflow.controller.resp.WorkflowBartenderChatResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo.Ingredient;
import cool.drinkup.drinkup.workflow.model.UserWine;

@Mapper(componentModel = "spring")
public interface UserWineMapper {

    Logger log = LoggerFactory.getLogger(UserWineMapper.class);
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "chatBotResponse.ingredients", target = "ingredients", qualifiedByName = "IngredientListToJsonString")
    @Mapping(source = "chatBotResponse.tagBaseSpirit", target = "tagBaseSpirit", qualifiedByName = "JsonStringListToString")
    @Mapping(source = "chatBotResponse.tagFlavor", target = "tagFlavor", qualifiedByName = "JsonStringListToString")
    @Mapping(source = "chatBotResponse.tagsOthers", target = "tagsOthers", qualifiedByName = "JsonStringListToString")
    @Mapping(source = "userId", target = "userId")
    UserWine toUserWine(WorkflowBartenderChatResp chatBotResponse, Long userId);

    @Named("JsonStringListToString")
    default String jsonToStringList(List<String> json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to String: {}", e.getMessage());
            return null;
        }
    }

    @Named("IngredientListToJsonString")
    default String ingredientListToJsonString(List<WorkflowUserWineVo.Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(ingredients);
        } catch (JsonProcessingException e) {
            log.error("Error converting IngredientList to JSON String: {}", e.getMessage());
            return null;
        }
    }

    @Mapping(source = "ingredients", target = "ingredients", qualifiedByName = "jsonToIngredientsList")
    @Mapping(source = "tagBaseSpirit", target = "tagBaseSpirit", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagFlavor", target = "tagFlavor", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagsOthers", target = "tagsOthers", qualifiedByName = "jsonToStringList")
    WorkflowUserWineVo toUserWineVo(UserWine userWine);

    @Named("jsonToIngredientsList")
    default List<Ingredient> jsonToIngredientsList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<WorkflowUserWineVo.Ingredient>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    @Named("jsonToStringList")
    default List<String> jsonToStringList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }
}
