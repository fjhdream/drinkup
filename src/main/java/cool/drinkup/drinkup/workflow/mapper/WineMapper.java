package cool.drinkup.drinkup.workflow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo.Ingredient;
import cool.drinkup.drinkup.workflow.model.Wine;

@Mapper(componentModel = "spring")
public interface WineMapper {
    
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(source = "ingredients", target = "ingredients", qualifiedByName = "jsonToIngredientsList")
    @Mapping(source = "tagBaseSpirit", target = "tagBaseSpirit", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagFlavor", target = "tagFlavor", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagsOthers", target = "tagsOthers", qualifiedByName = "jsonToStringList")
    WorkflowUserWineVo toWineVo(Wine wine);

    @Named("jsonToIngredientsList")
    default List<Ingredient> jsonToIngredientsList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<Ingredient>>() {});
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
