package cool.drinkup.drinkup.workflow.internal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowUserWineVo.Ingredient;
import cool.drinkup.drinkup.workflow.internal.model.Wine;
import cool.drinkup.drinkup.workflow.internal.service.image.ImageService;

@Mapper(componentModel = "spring")
public abstract class WineMapper {
    
    protected ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Autowired
    protected ImageService imageService;

    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(source = "ingredients", target = "ingredients", qualifiedByName = "jsonToIngredientsList")
    @Mapping(source = "tagBaseSpirit", target = "tagBaseSpirit", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagFlavor", target = "tagFlavor", qualifiedByName = "jsonToStringList")
    @Mapping(source = "tagsOthers", target = "tagsOthers", qualifiedByName = "jsonToStringList")
    @Mapping(source = "image", target = "image", qualifiedByName = "imageToUrl")
    public abstract WorkflowUserWineVo toWineVo(Wine wine);

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

    @Named("imageToUrl")
    protected String imageToUrl(String imageId) {
        return imageService.getImageUrl(imageId);
    }
}
