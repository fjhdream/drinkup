package cool.drinkup.drinkup.workflow.internal.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mzt.logapi.starter.annotation.LogRecord;

import cool.drinkup.drinkup.common.log.event.AIChatEvent;
import cool.drinkup.drinkup.common.log.event.WineEvent;
import cool.drinkup.drinkup.shared.dto.WorkflowBartenderChatDto;
import cool.drinkup.drinkup.shared.spi.CommonResp;
import cool.drinkup.drinkup.wine.spi.WorkflowWineResp;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowBartenderChatReq;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowStockRecognitionReq;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowUserChatReq;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowUserReq;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowStockRecognitionResp;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowUserChatResp;
import cool.drinkup.drinkup.workflow.internal.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Tag(name = "调酒工作流", description = "调酒工作流相关操作API")
public class WorkflowController {

    private final WorkflowService workflowService;

    @LogRecord(
        type = WineEvent.WINE,
        subType = WineEvent.BehaviorEvent.COCKTAIL_REQUEST,
        bizNo = "null",
        success = "用户调酒单请求成功, 请求内容：{{#userInput.userInput}}"
    )
    @Operation(
        summary = "处理调酒单请求",
        description = "处理用户输入的鸡尾酒相关工作流"
    )
    @ApiResponse(responseCode = "200", description = "Successfully processed cocktail request")
    @PostMapping("/cocktail")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<WorkflowWineResp>> processCocktailRequest(
        @Parameter(description = "User input for cocktail workflow")
        @RequestBody WorkflowUserReq userInput
    ) {
        var resp = workflowService.processCocktailRequest(userInput);
        return ResponseEntity.ok(CommonResp.success(resp));
    }

    @LogRecord(
        type = AIChatEvent.AI_CHAT,
        subType = AIChatEvent.BehaviorEvent.AI_CHAT,
        bizNo = "null",
        success = "用户AI聊天成功, 用户请求：{{#userInput.messages.$[#this != null].content}}",
        extra = "{{@logExtraUtil.getLogExtra(#_ret.body.data)}}"
    )
    @Operation(
        summary = "与机器人聊天",
        description = "与机器人进行对话"
    )
    @ApiResponse(responseCode = "200", description = "Successfully chatted with the bot")
    @PostMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<WorkflowUserChatResp>> chat(@RequestBody WorkflowUserChatReq userInput) {
        var resp = workflowService.chat(userInput);
        if (resp == null) {
            return ResponseEntity.ok(CommonResp.error("Error chatting with the bot"));
        }
        return ResponseEntity.ok(CommonResp.success(resp));
    }

    @LogRecord(
        type = AIChatEvent.AI_CHAT,
        subType = AIChatEvent.BehaviorEvent.BARTENDER_CHAT,
        bizNo = "{{#_ret.body.data.id}}",
        success = "用户调酒师聊天成功，生成酒单：{{#_ret.body.data.name}}",
        extra = "{{@logExtraUtil.getLogExtra(#bartenderInput)}}"
    )
    @Operation(
        summary = "与调酒师聊天",
        description = "与调酒师进行对话"
    )
    @ApiResponse(responseCode = "200", description = "Successfully chatted with the bartender")
    @PostMapping("/bartender")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<WorkflowBartenderChatDto>> mixDrink(@RequestBody WorkflowBartenderChatReq bartenderInput) {
        var resp = workflowService.mixDrink(bartenderInput);
        if (resp == null) {
            return ResponseEntity.ok(CommonResp.error("Error mixing drink"));
        }
        return ResponseEntity.ok(CommonResp.success(resp));
    }

    @LogRecord(
        type = AIChatEvent.AI_CHAT,
        subType = AIChatEvent.BehaviorEvent.STOCK_RECOGNITION,
        bizNo = "{{#barId}}",
        success = "用户库存识别成功，识别到{{#_ret.body.data.recognizedStocks.size()}}种库存"
    )
    @Operation(summary = "库存识别", description = "通过图片识别库存")
    @ApiResponse(responseCode = "200", description = "Successfully recognized stock from image")
    @PostMapping(value = "/recognize-stock/{barId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResp<WorkflowStockRecognitionResp>> recognizeStock(
            @Parameter(description = "Bar ID") @PathVariable("barId") Long barId,
            @Parameter(description = "Image file for stock recognition") @RequestBody WorkflowStockRecognitionReq req) {
        req.setBarId(barId);
        var resp = workflowService.recognizeStock(req);
        if (resp == null) {
            return ResponseEntity.ok(CommonResp.error("Error recognizing stock"));
        }
        return ResponseEntity.ok(CommonResp.success(resp));
    }

}