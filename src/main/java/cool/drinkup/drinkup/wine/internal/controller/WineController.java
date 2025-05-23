package cool.drinkup.drinkup.wine.internal.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cool.drinkup.drinkup.wine.internal.controller.resp.CommonResp;
import cool.drinkup.drinkup.wine.internal.controller.resp.RandomWineResp;
import cool.drinkup.drinkup.wine.internal.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.wine.internal.mapper.UserWineMapper;
import cool.drinkup.drinkup.wine.internal.mapper.WineMapper;
import cool.drinkup.drinkup.wine.internal.model.UserWine;
import cool.drinkup.drinkup.wine.internal.model.Wine;
import cool.drinkup.drinkup.wine.internal.rag.DataLoaderService;
import cool.drinkup.drinkup.wine.internal.service.UserWineService;
import cool.drinkup.drinkup.wine.internal.service.WineService;
import cool.drinkup.drinkup.wine.spi.WorkflowWineVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/wines")
@Tag(name = "酒管理", description = "提供酒相关的API接口")
public class WineController {

    private final WineService wineService;
    private final UserWineService userWineService;
    private final WineMapper wineMapper;
    private final UserWineMapper userWineMapper;
    private final DataLoaderService dataLoaderService;

    public WineController(WineService wineService, UserWineService userWineService, WineMapper wineMapper, UserWineMapper userWineMapper, DataLoaderService dataLoaderService) {
        this.wineService = wineService;
        this.userWineService = userWineService;
        this.wineMapper = wineMapper;
        this.userWineMapper = userWineMapper;
        this.dataLoaderService = dataLoaderService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询酒", description = "通过酒的唯一标识符查询详细信息")
    @Parameter(name = "id", description = "酒ID", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取酒信息"),
        @ApiResponse(responseCode = "404", description = "未找到指定ID的酒")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<WorkflowWineVo>> getWineById(@PathVariable Long id) {
        Wine wineById = wineService.getWineById(id);
        if (wineById == null) {
            return ResponseEntity.notFound().build();
        }
        WorkflowWineVo wineVo = wineMapper.toWineVo(wineById);
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
    public ResponseEntity<CommonResp<Page<WorkflowWineVo>>> getWinesByTag(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String iba,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Wine> wines = wineService.getWinesByTag(tag, iba, pageRequest);
        Page<WorkflowWineVo> wineVos = wines.map(wineMapper::toWineVo);
        return ResponseEntity.ok(CommonResp.success(wineVos));
    }

    @GetMapping("/user-wine")
    @Operation(summary = "查询用户酒列表", description = "查询当前用户的所有酒")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<Page<WorkflowUserWineVo>>> getUserWine(@RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        Page<UserWine> userWine = userWineService.getUserWine(pageRequest);
        Page<WorkflowUserWineVo> userWineVos = userWine.map(userWineMapper::toWorkflowUserWineVo);
        return ResponseEntity.ok(CommonResp.success(userWineVos));
    }

    @Operation(
        summary = "加载酒类数据到向量数据库",
        description = "将酒类相关数据加载到系统中"
    )
    @ApiResponse(responseCode = "200", description = "Successfully loaded wine data")
    @PostMapping("/load-wine")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> loadWine() {
        dataLoaderService.loadData();
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/random")
    @Operation(summary = "随机来一杯", description = "根据类型随机获取一杯酒，支持三种类型：mixed(完全随机)、user(用户酒库随机)、iba(AI酒单随机)")
    @Parameter(name = "type", description = "随机类型：mixed(完全随机)、user(用户酒库随机)、iba(酒单随机)", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取随机酒"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "未找到符合条件的酒")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<RandomWineResp>> getRandomWine(@RequestParam String type) {
        switch (type.toLowerCase()) {
            case "mixed":
                // 完全随机：从 Wine 和 UserWine 中随机选择
                return getRandomMixedWine();
            case "user":
                // 用户酒库随机：从 UserWine 中随机选择
                return getRandomUserWine();
            case "iba":
                // IBA酒单随机：从 Wine 中随机选择
                return getRandomWine();
            default:
                return ResponseEntity.badRequest()
                    .body(CommonResp.error("无效的类型参数，支持的类型：mixed、user、iba"));
        }
    }
    
    private ResponseEntity<CommonResp<RandomWineResp>> getRandomMixedWine() {
        // 随机选择从 Wine 或 UserWine 中获取
        if (Math.random() < 0.5) {
            return getRandomWine();
        } else {
            ResponseEntity<CommonResp<RandomWineResp>> userWineResult = getRandomUserWine();
            // 如果用户酒库为空，则返回IBA酒单  
            if (userWineResult.getStatusCode().is4xxClientError()) {
                return getRandomWine();
            }
            return userWineResult;
        }
    }
    
    private ResponseEntity<CommonResp<RandomWineResp>> getRandomUserWine() {
        UserWine randomUserWine = userWineService.getRandomUserWine();
        if (randomUserWine == null) {
            return ResponseEntity.status(404)
                .body(CommonResp.error("用户酒库为空"));
        }
        WorkflowUserWineVo userWineVo = userWineMapper.toWorkflowUserWineVo(randomUserWine);
        return ResponseEntity.ok(CommonResp.success(RandomWineResp.builder().type("user").wine(userWineVo).build()));
    }
    
    private ResponseEntity<CommonResp<RandomWineResp>> getRandomWine() {
        Wine randomWine = wineService.getRandomWine();
        if (randomWine == null) {
            return ResponseEntity.status(404)
                .body(CommonResp.error("IBA酒单为空"));
        }
        WorkflowWineVo wineVo = wineMapper.toWineVo(randomWine);
        return ResponseEntity.ok(CommonResp.success(RandomWineResp.builder().type("iba").wine(wineVo).build()));
    }
} 