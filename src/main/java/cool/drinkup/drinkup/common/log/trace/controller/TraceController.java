package cool.drinkup.drinkup.common.log.trace.controller;

import cool.drinkup.drinkup.common.log.trace.TraceService;
import cool.drinkup.drinkup.common.log.trace.controller.request.TraceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trace")
@RequiredArgsConstructor
@Tag(name = "Trace API", description = "埋点追踪接口")
public class TraceController {

    private final TraceService traceService;

    @PostMapping
    @Operation(summary = "发送埋点", description = "发送埋点数据进行追踪")
    public ResponseEntity<Void> trace(@Valid @RequestBody TraceRequest request) {
        traceService.trace(request.getType(), request.getSubType(), request.getBizNo(), request.getProperties());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/batch")
    @Operation(summary = "批量发送埋点", description = "批量发送多个埋点数据")
    public ResponseEntity<Void> traceBatch(@Valid @RequestBody TraceRequest[] requests) {
        for (TraceRequest request : requests) {
            traceService.trace(request.getType(), request.getSubType(), request.getBizNo(), request.getProperties());
        }
        return ResponseEntity.ok().build();
    }
}
