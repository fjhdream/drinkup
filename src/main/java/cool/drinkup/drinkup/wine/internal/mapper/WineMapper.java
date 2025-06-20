package cool.drinkup.drinkup.wine.internal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import cool.drinkup.drinkup.wine.internal.controller.resp.WorkflowWineVo;
import cool.drinkup.drinkup.wine.internal.controller.resp.WorkflowWineVo.Ingredient;
import cool.drinkup.drinkup.wine.internal.model.Wine;

@Mapper(componentModel = "spring", uses = ImageServiceMapper.class)
public abstract class WineMapper {
    
    protected ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(source = "ingredients", target = "ingredients", qualifiedByName = "jsonToIngredientsList")
    @Mapping(source = "tagBaseSpirit", target = "tagBaseSpirit", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagFlavor", target = "tagFlavor", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagsOthers", target = "tagsOthers", qualifiedByName = "jsonToStringList")
    @Mapping(source = "image", target = "image", qualifiedByName = "imageToUrl")
    @Mapping(source = "favoriteCount", target = "favoriteCount")
    @Mapping(target = "favoriteType", expression = "java(cool.drinkup.drinkup.favorite.spi.FavoriteType.WINE)")
    public abstract WorkflowWineVo toWineVo(Wine wine);

    @Named("jsonToIngredientsList")
    protected List<Ingredient> jsonToIngredientsList(String json) {
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
} 