package cool.drinkup.drinkup.workflow.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cool.drinkup.drinkup.workflow.controller.resp.CommonResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.workflow.mapper.UserWineMapper;
import cool.drinkup.drinkup.workflow.mapper.WineMapper;
import cool.drinkup.drinkup.workflow.model.UserWine;
import cool.drinkup.drinkup.workflow.model.Wine;
import cool.drinkup.drinkup.workflow.service.UserWineService;
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
    private UserWineService userWineService;
    private WineMapper wineMapper;
    private UserWineMapper userWineMapper;

    public WineController(WineService wineService, UserWineService userWineService, WineMapper wineMapper, UserWineMapper userWineMapper) {
        this.wineService = wineService;
        this.userWineService = userWineService;
        this.wineMapper = wineMapper;
        this.userWineMapper = userWineMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询酒", description = "通过酒的唯一标识符查询详细信息")
    @Parameter(name = "id", description = "酒ID", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取酒信息"),
        @ApiResponse(responseCode = "404", description = "未找到指定ID的酒")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<WorkflowUserWineVo>> getWineById(@PathVariable Long id) {
        Wine wineById = wineService.getWineById(id);
        if (wineById == null) {
            return ResponseEntity.notFound().build();
        }
        WorkflowUserWineVo wineVo = wineMapper.toWineVo(wineById);
        return ResponseEntity.ok(CommonResp.success(wineVo));
    }

    @GetMapping("/by-tag")
    @Operation(summary = "查询酒列表", description = "通过标签查询酒列表，支持分页。如果不提供标签则返回所有酒")
    @Parameter(name = "tag", description = "标签名称，可选参数", required = false)
    @Parameter(name = "page", description = "页码，从0开始", required = false)
    @Parameter(name = "size", description = "每页大小", required = false)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取酒列表"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<Page<WorkflowUserWineVo>>> getWinesByTag(
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Wine> wines = wineService.getWinesByTag(tag, pageRequest);
        Page<WorkflowUserWineVo> wineVos = wines.map(wineMapper::toWineVo);
        return ResponseEntity.ok(CommonResp.success(wineVos));
    }

    @GetMapping("/user-wine")
    @Operation(summary = "查询用户酒列表", description = "查询当前用户的所有酒")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<Page<WorkflowUserWineVo>>> getUserWine(@RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<UserWine> userWine = userWineService.getUserWine(pageRequest);
        Page<WorkflowUserWineVo> userWineVos = userWine.map(userWineMapper::toUserWineVo);
        return ResponseEntity.ok(CommonResp.success(userWineVos));
    }
}