package cool.drinkup.drinkup.workflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cool.drinkup.drinkup.workflow.controller.resp.CommonResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.workflow.mapper.WineMapper;
import cool.drinkup.drinkup.workflow.model.Wine;
import cool.drinkup.drinkup.workflow.service.WineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/wines")
@Tag(name = "酒管理", description = "提供酒相关的API接口")
public class WineController {

    private WineService wineService;
    private WineMapper wineMapper;

    public WineController(WineService wineService, WineMapper wineMapper) {
        this.wineService = wineService;
        this.wineMapper = wineMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询酒", description = "通过酒的唯一标识符查询详细信息")
    @Parameter(name = "id", description = "酒ID", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取酒信息"),
        @ApiResponse(responseCode = "404", description = "未找到指定ID的酒")
    })
    public ResponseEntity<CommonResp<?>> getWineById(@PathVariable Long id) {
        Wine wineById = wineService.getWineById(id);
        if (wineById == null) {
            return ResponseEntity.notFound().build();
        }
        WorkflowUserWineVo wineVo = wineMapper.toWineVo(wineById);
        return ResponseEntity.ok(CommonResp.success(wineVo));
    }
} 