package cool.drinkup.drinkup.workflow.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cool.drinkup.drinkup.workflow.controller.req.WorkflowBartenderChatReq;
import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserChatReq;
import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserReq;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowBartenderChatResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserChatResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWIneResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.workflow.mapper.WineMapper;
import cool.drinkup.drinkup.workflow.model.Wine;
import cool.drinkup.drinkup.workflow.respository.WineRepository;
import cool.drinkup.drinkup.workflow.service.bartender.BartenderService;
import cool.drinkup.drinkup.workflow.service.chat.ChatBotService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class WorkflowService {

    private final VectorStore vectorStore;

    private final WineRepository wineRepository;

    private final WineMapper wineMapper;

    private final ChatBotService chatBotService;

    private final BartenderService bartenderService;

    private final ObjectMapper objectMapper;

    public WorkflowService(VectorStore vectorStore, WineRepository wineRepository, WineMapper wineMapper, 
            ChatBotService chatBotService, BartenderService bartenderService,@Qualifier("snakeCaseObjectMapper") ObjectMapper objectMapper) {
        this.vectorStore = vectorStore;
        this.wineRepository = wineRepository;
        this.wineMapper = wineMapper;
        this.chatBotService = chatBotService;
        this.bartenderService = bartenderService;
        this.objectMapper = objectMapper;
    }

    public WorkflowUserWIneResp processCocktailRequest(WorkflowUserReq userInput) {
        String userInputText = userInput.getUserInput();
        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder().query(userInputText).topK(2).build());
        log.info("Results: {}", results);
        List<Long> wineIds = results.stream()
                .map(Document::getMetadata)
                .map(metadata -> Long.valueOf(metadata.get("wineId").toString()))
                .collect(Collectors.toList());
        List<Wine> wines = wineRepository.findAllById(wineIds);
        log.info("Wines: {}", wines);
        List<WorkflowUserWineVo> workflowUserWineVos = wines.stream()
                .map(wineMapper::toWineVo)
                .collect(Collectors.toList());
        WorkflowUserWIneResp workflowUserWIneResp = new WorkflowUserWIneResp();
        workflowUserWIneResp.setWines(workflowUserWineVos);
        return workflowUserWIneResp;
    }

    public WorkflowUserChatResp chat(WorkflowUserChatReq userInput) {
        var chatWithUser = chatBotService.chat(userInput.getMessages());
        var json = extractJson(chatWithUser);
        try {
            var chatBotResponse = objectMapper.readValue(json, WorkflowUserChatResp.class);
            return chatBotResponse;
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            return null;
        }
    }

    public void chatStream(WorkflowUserChatReq userInput, SseEmitter emitter) throws IOException {
        StringBuilder fullResponse = new StringBuilder();
        
        // Set up error handler for the emitter
        emitter.onError(ex -> {
            log.error("Emitter error: {}", ex.getMessage());
        });
        
        emitter.onCompletion(() -> {
            log.info("Emitter completed");
        });

        // Use the streaming chat method
        CompletableFuture<Void> future = chatBotService.chatStream(userInput.getMessages(), chunk -> {
            try {
                // Append the chunk to the full response
                fullResponse.append(chunk);
                
                // Send the chunk to the client with proper error handling
                try {
                    emitter.send(SseEmitter.event()
                        .name("chunk")
                        .data(chunk));
                } catch (IOException e) {
                    log.error("Error sending chunk, client likely disconnected: {}", e.getMessage());
                    try {
                        emitter.complete();
                    } catch (Exception ex) {
                        log.warn("Error completing emitter: {}", ex.getMessage());
                    }
                    // Throw exception to propagate error upward
                    throw e;
                }
            } catch (Exception e) {
                log.error("Error processing chunk: {}", e.getMessage());
                emitter.completeWithError(e);
            }
        });
    }

    public Flux<String> chatStreamFlux(WorkflowUserChatReq userInput) {
        return chatBotService.chatStreamFlux(userInput.getMessages());
    }

    private String extractJson(String chatWithUser) {
        if (!chatWithUser.contains("```json")) {
            return chatWithUser;
        }
        // Extract JSON content between ```json and ``` markers
        String jsonPattern = "```json\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(jsonPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(chatWithUser);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // If no JSON found in the expected format, return the original string
        // This handles cases where the response might not be properly formatted
        return chatWithUser;
    }

    public WorkflowBartenderChatResp mixDrink(WorkflowBartenderChatReq bartenderInput) {
        var chatWithBartender = bartenderService.generateDrink(bartenderInput.getMessages());
        var json = extractJson(chatWithBartender);
        try {
            var chatBotResponse = objectMapper.readValue(json, WorkflowBartenderChatResp.class);
            return chatBotResponse;
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            return null;
        }
    }
}
