package cool.drinkup.drinkup.wine.internal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import cool.drinkup.drinkup.shared.dto.WorkflowBartenderChatDto;
import cool.drinkup.drinkup.shared.dto.WorkflowWineVo;
import cool.drinkup.drinkup.wine.internal.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.wine.internal.model.UserWine;

@Mapper(componentModel = "spring", uses = ImageServiceMapper.class)
public abstract class UserWineMapper {

    protected static final Logger log = LoggerFactory.getLogger(UserWineMapper.class);
    protected ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "chatBotResponse.ingredients", target = "ingredients", qualifiedByName = "IngredientListToJsonString")
    @Mapping(source = "chatBotResponse.tagBaseSpirit", target = "tagBaseSpirit", qualifiedByName = "JsonStringListToString")
    @Mapping(source = "chatBotResponse.tagFlavor", target = "tagFlavor", qualifiedByName = "JsonStringListToString")
    @Mapping(source = "chatBotResponse.tagsOthers", target = "tagsOthers", qualifiedByName = "JsonStringListToString")
    @Mapping(source = "userId", target = "userId")
    public abstract UserWine toUserWine(WorkflowBartenderChatDto chatBotResponse, Long userId);

    @Named("JsonStringListToString")
    protected String jsonToStringList(List<String> json) {
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
    protected String ingredientListToJsonString(List<WorkflowWineVo.Ingredient> ingredients) {
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

    @Mapping(source = "ingredients", target = "ingredients", qualifiedByName = "jsonToWineIngredientsList")
    @Mapping(source = "tagBaseSpirit", target = "tagBaseSpirit", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagFlavor", target = "tagFlavor", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagsOthers", target = "tagsOthers", qualifiedByName = "jsonToStringList")
    @Mapping(source = "image", target = "image", qualifiedByName = "imageToUrl")
    @Mapping(source = "createDate", target = "createDate", qualifiedByName = "dateToString")
    @Mapping(source = "updateDate", target = "updateDate", qualifiedByName = "dateToString")
    @Mapping(target = "tagIba", ignore = true)
    public abstract WorkflowWineVo toUserWineVo(UserWine userWine);

    @Mapping(source = "ingredients", target = "ingredients", qualifiedByName = "jsonToUserWineIngredientsList")
    @Mapping(source = "tagBaseSpirit", target = "tagBaseSpirit", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagFlavor", target = "tagFlavor", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagsOthers", target = "tagsOthers", qualifiedByName = "jsonToStringList")
    @Mapping(source = "image", target = "image", qualifiedByName = "imageToUrl")
    @Mapping(source = "createDate", target = "createDate", qualifiedByName = "dateToString")
    @Mapping(source = "updateDate", target = "updateDate", qualifiedByName = "dateToString")
    public abstract WorkflowUserWineVo toWorkflowUserWineVo(UserWine userWine);

    @Named("jsonToWineIngredientsList")
    protected List<WorkflowWineVo.Ingredient> jsonToWineIngredientsList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<WorkflowWineVo.Ingredient>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    @Named("jsonToUserWineIngredientsList")
    protected List<WorkflowUserWineVo.Ingredient> jsonToUserWineIngredientsList(String json) {
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
    protected List<String> jsonToStringList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    @Named("dateToString")
    protected String dateToString(ZonedDateTime date) {
        if (date == null) {
            return null;
        }
        // Convert UTC time to UTC+8 (Asia/Shanghai timezone)
        ZonedDateTime shanghaiTime = date.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
        return shanghaiTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
} 