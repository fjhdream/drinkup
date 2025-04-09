package cool.drinkup.drinkup.workflow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import cool.drinkup.drinkup.workflow.controller.req.BarCreateReq;
import cool.drinkup.drinkup.workflow.model.Bar;

@Mapper(componentModel = "spring")
public interface BarMapper {

    @Mapping(target = "id", ignore = true)
    Bar toBar(BarCreateReq barCreateReq);
}
