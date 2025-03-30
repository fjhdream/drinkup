package cool.drinkup.drinkup.workflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserReq;
import cool.drinkup.drinkup.workflow.controller.resp.CommonResp;
import cool.drinkup.drinkup.workflow.service.WorkflowService;
import cool.drinkup.drinkup.workflow.service.rag.DataLoaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Tag(name = "调酒工作流", description = "调酒工作流相关操作API")
public class WorkflowController {

    private final DataLoaderService dataLoaderService;

    private final WorkflowService workflowService;

    @Operation(
        summary = "Process cocktail request",
        description = "Processes user input for cocktail related workflow"
    )
    @ApiResponse(responseCode = "200", description = "Successfully processed cocktail request")
    @PostMapping("/cocktail")
    public ResponseEntity<CommonResp<?>> processCocktailRequest(
        @Parameter(description = "User input for cocktail workflow") 
        @RequestBody WorkflowUserReq userInput
    ) {
        var resp = workflowService.processCocktailRequest(userInput);
        return ResponseEntity.ok(CommonResp.success(resp));
    }

    @Operation(
        summary = "Load wine data",
        description = "Loads wine related data into the system"
    )
    @ApiResponse(responseCode = "200", description = "Successfully loaded wine data")
    @PostMapping("/load-wine")
    public ResponseEntity<?> loadWine() {
        dataLoaderService.loadData();
        return ResponseEntity.ok().build();
    }
    
} 