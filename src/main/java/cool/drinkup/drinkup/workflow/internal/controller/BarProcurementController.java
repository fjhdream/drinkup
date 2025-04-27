package cool.drinkup.drinkup.workflow.internal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import cool.drinkup.drinkup.workflow.internal.controller.req.BarProcurementCreateReq;
import cool.drinkup.drinkup.workflow.internal.controller.req.BarProcurementUpdateReq;
import cool.drinkup.drinkup.workflow.internal.controller.resp.CommonResp;
import cool.drinkup.drinkup.workflow.internal.model.BarProcurement;
import cool.drinkup.drinkup.workflow.internal.service.procurement.BarProcurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "吧台采购管理", description = "吧台采购相关接口")
@RestController
@RequestMapping("/bar-procurement")
public class BarProcurementController {
    
    private final BarProcurementService barProcurementService;

    public BarProcurementController(BarProcurementService barProcurementService) {
        this.barProcurementService = barProcurementService;
    }

    @Operation(summary = "获取吧台采购", description = "获取指定吧台的所有采购信息")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{barId}")
    public ResponseEntity<CommonResp<List<BarProcurement>>> getBarProcurement(
            @Parameter(description = "吧台ID") @PathVariable Long barId) {
        List<BarProcurement> barProcurement = barProcurementService.getBarProcurement(barId);
        return ResponseEntity.ok(CommonResp.success(barProcurement));
    }

    @Operation(summary = "创建吧台采购", description = "为指定吧台创建新的采购记录")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{barId}")
    public ResponseEntity<CommonResp<List<BarProcurement>>> createBarProcurement(
            @Parameter(description = "吧台ID") @PathVariable Long barId, 
            @RequestBody BarProcurementCreateReq barProcurementCreateReq) {
        List<BarProcurement> createdBarProcurements = barProcurementService.createBarProcurement(barId, barProcurementCreateReq);
        return ResponseEntity.ok(CommonResp.success(createdBarProcurements));
    }

    @Operation(summary = "更新吧台采购", description = "更新指定吧台的采购记录")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{barId}/{procurementId}")
    public ResponseEntity<CommonResp<BarProcurement>> updateBarProcurement(
            @Parameter(description = "吧台ID") @PathVariable Long barId,
            @Parameter(description = "采购ID") @PathVariable Long procurementId,
            @RequestBody BarProcurementUpdateReq barProcurementUpdateReq) {
        BarProcurement updatedBarProcurement = barProcurementService.updateBarProcurement(barId, procurementId, barProcurementUpdateReq);
        return ResponseEntity.ok(CommonResp.success(updatedBarProcurement));
    }

    @Operation(summary = "删除吧台采购", description = "删除指定吧台的采购记录")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{barId}/{procurementId}")
    public ResponseEntity<CommonResp<Void>> deleteBarProcurement(
            @Parameter(description = "吧台ID") @PathVariable Long barId,
            @Parameter(description = "采购ID") @PathVariable Long procurementId) {
        barProcurementService.deleteBarProcurement(barId, procurementId);
        return ResponseEntity.ok(CommonResp.success(null));
    }
} 