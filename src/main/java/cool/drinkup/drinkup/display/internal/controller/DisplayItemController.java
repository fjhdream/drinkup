package cool.drinkup.drinkup.display.internal.controller;

import cool.drinkup.drinkup.display.api.DisplayItemService;
import cool.drinkup.drinkup.display.internal.controller.resp.DisplayItemVo;
import cool.drinkup.drinkup.display.internal.enums.DisplayType;
import cool.drinkup.drinkup.shared.spi.CommonResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/display-items")
@RequiredArgsConstructor
@Tag(name = "展示项管理", description = "提供Banner和提示弹窗相关的API接口")
public class DisplayItemController {

    private final DisplayItemService displayItemService;

    @GetMapping
    @Operation(summary = "获取有效展示项列表", description = "获取当前有效的所有展示项，包括Banner和提示弹窗")
    public CommonResp<List<DisplayItemVo>> getActiveDisplayItems(
            @Parameter(description = "展示类型筛选") @RequestParam(required = false) DisplayType type) {

        List<DisplayItemVo> items;
        if (type != null) {
            items = displayItemService.getActiveDisplayItemsByType(type);
        } else {
            items = displayItemService.getActiveDisplayItems();
        }

        return CommonResp.success(items);
    }
}
