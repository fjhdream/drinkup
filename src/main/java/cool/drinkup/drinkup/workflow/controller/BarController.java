package cool.drinkup.drinkup.workflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import cool.drinkup.drinkup.workflow.controller.req.BarCreateReq;
import cool.drinkup.drinkup.workflow.controller.resp.CommonResp;
import cool.drinkup.drinkup.workflow.model.Bar;
import cool.drinkup.drinkup.workflow.service.bar.BarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "吧台管理", description = "吧台相关接口")
@RestController
@RequestMapping("/bar")
public class BarController {
    
    private final BarService barService;

    public BarController(BarService barService) {
        this.barService = barService;
    }
    
    @Operation(summary = "创建吧台", description = "创建一个新的吧台")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<CommonResp<Bar>> createBar(@RequestBody BarCreateReq barCreateReq) {
        Bar bar = barService.createBar(barCreateReq);
        return ResponseEntity.ok(CommonResp.success(bar));
    }

    @Operation(summary = "获取吧台列表", description = "获取当前用户的所有吧台")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<CommonResp<List<Bar>>> getBar() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Bar> bars = barService.getUserBar(username);
        return ResponseEntity.ok(CommonResp.success(bars));
    }
}
