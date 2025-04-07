package cool.drinkup.drinkup.workflow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cool.drinkup.drinkup.workflow.controller.req.WorkflowBartenderChatReq;
import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserChatReq;
import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserReq;
import cool.drinkup.drinkup.workflow.controller.resp.CommonResp;
import cool.drinkup.drinkup.workflow.service.WorkflowService;
import cool.drinkup.drinkup.workflow.service.rag.DataLoaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Tag(name = "调酒工作流", description = "调酒工作流相关操作API")
public class WorkflowController {

    private final DataLoaderService dataLoaderService;
    private final WorkflowService workflowService;
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);

    @Operation(
        summary = "处理调酒单请求",
        description = "处理用户输入的鸡尾酒相关工作流"
    )
    @ApiResponse(responseCode = "200", description = "Successfully processed cocktail request")
    @PostMapping("/cocktail")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<?>> processCocktailRequest(
        @Parameter(description = "User input for cocktail workflow") 
        @RequestBody WorkflowUserReq userInput
    ) {
        var resp = workflowService.processCocktailRequest(userInput);
        return ResponseEntity.ok(CommonResp.success(resp));
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

    @Operation(
        summary = "与机器人聊天",
        description = "与机器人进行对话"
    )
    @ApiResponse(responseCode = "200", description = "Successfully chatted with the bot")
    @PostMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<?>> chat(@RequestBody WorkflowUserChatReq userInput) {
        var resp = workflowService.chat(userInput);
        if (resp == null) {
            return ResponseEntity.ok(CommonResp.error("Error chatting with the bot"));
        }
        return ResponseEntity.ok(CommonResp.success(resp));
    }

    @Operation(
        summary = "与机器人聊天(流式)",
        description = "与机器人进行对话，使用流式响应"
    )
    @ApiResponse(responseCode = "200", description = "Successfully chatted with the bot using streaming")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Flux<String> chatStream(@RequestBody WorkflowUserChatReq userInput) {
        return workflowService.chatStreamFlux(userInput);
    }

    @Operation(
        summary = "与调酒师聊天",
        description = "与调酒师进行对话"
    )
    @ApiResponse(responseCode = "200", description = "Successfully chatted with the bartender")
    @PostMapping("/bartender")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<?>> mixDrink(@RequestBody WorkflowBartenderChatReq bartenderInput) {
        var resp = workflowService.mixDrink(bartenderInput);
        if (resp == null) {
            return ResponseEntity.ok(CommonResp.error("Error mixing drink"));
        }
        return ResponseEntity.ok(CommonResp.success(resp));
    }
}