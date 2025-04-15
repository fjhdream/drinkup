package cool.drinkup.drinkup.workflow.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cool.drinkup.drinkup.workflow.controller.req.WorkflowBartenderChatReq;
import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserChatReq;
import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserReq;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowBartenderChatResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserChatResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.workflow.mapper.WineMapper;
import cool.drinkup.drinkup.workflow.model.Bar;
import cool.drinkup.drinkup.workflow.model.Wine;
import cool.drinkup.drinkup.workflow.repository.WineRepository;
import cool.drinkup.drinkup.workflow.service.bar.BarService;
import cool.drinkup.drinkup.workflow.service.bartender.BartenderService;
import cool.drinkup.drinkup.workflow.service.bartender.dto.BartenderParams;
import cool.drinkup.drinkup.workflow.service.chat.ChatBotService;
import cool.drinkup.drinkup.workflow.service.chat.dto.ChatParams;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class WorkflowService {

    private final VectorStore vectorStore;

    private final WineRepository wineRepository;

    private final WineMapper wineMapper;

    private final UserWineService userWineService;

    private final ChatBotService chatBotService;

    private final BartenderService bartenderService;

    private final BarService barService;

    private final ObjectMapper objectMapper;

    public WorkflowService(VectorStore vectorStore, WineRepository wineRepository, WineMapper wineMapper,
            UserWineService userWineService, ChatBotService chatBotService, BartenderService bartenderService, BarService barService,
            @Qualifier("snakeCaseObjectMapper") ObjectMapper objectMapper) {
        this.vectorStore = vectorStore;
        this.wineRepository = wineRepository;
        this.wineMapper = wineMapper;
        this.userWineService = userWineService;
        this.chatBotService = chatBotService;
        this.bartenderService = bartenderService;
        this.barService = barService;
        this.objectMapper = objectMapper;
    }

    public WorkflowUserWineResp processCocktailRequest(WorkflowUserReq userInput) {
        String userInputText = userInput.getUserInput();
        List<Document> results = vectorStore
                .similaritySearch(SearchRequest.builder().query(userInputText).topK(2).build());
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
        WorkflowUserWineResp workflowUserWIneResp = new WorkflowUserWineResp();
        workflowUserWIneResp.setWines(workflowUserWineVos);
        return workflowUserWIneResp;
    }

    public WorkflowUserChatResp chat(WorkflowUserChatReq userInput) {
        List<Bar> bars = barService.getUserBarByBarIds(userInput.getBarIds());
        ChatParams chatParams = buildChatParams(bars);
        var chatWithUser = chatBotService.chat(userInput.getMessages(), chatParams);
        var json = extractJson(chatWithUser);
        try {
            var chatBotResponse = objectMapper.readValue(json, WorkflowUserChatResp.class);
            return chatBotResponse;
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            return null;
        }
    }

    public Flux<String> chatStreamFlux(WorkflowUserChatReq userInput) {
        List<Bar> bars = barService.getUserBarByBarIds(userInput.getBarIds());
        ChatParams chatParams = buildChatParams(bars);
        return chatBotService.chatStreamFlux(userInput.getMessages(), chatParams);
    }

    private String extractJson(String chatWithUser) {
        if ( !chatWithUser.contains("```json")) {
            return chatWithUser;
        }
        // Extract JSON content between ```json and ``` markers
        String jsonPattern = "```json\\s*(.*?)\\s*```";
        Pattern pattern = Pattern.compile(jsonPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(chatWithUser);

        if ( matcher.find()) {
            return matcher.group(1).trim();
        }

        // If no JSON found in the expected format, return the original string
        // This handles cases where the response might not be properly formatted
        return chatWithUser;
    }

    public WorkflowBartenderChatResp mixDrink(WorkflowBartenderChatReq bartenderInput) {
        var bartenderParam = buildBartenderParams(bartenderInput);
        var chatWithBartender = bartenderService.generateDrink(bartenderInput.getMessages(), bartenderParam);
        var json = extractJson(chatWithBartender);
        try {
            var chatBotResponse = objectMapper.readValue(json, WorkflowBartenderChatResp.class);
            userWineService.saveUserWine(chatBotResponse);
            return chatBotResponse;
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            return null;
        }
    }

    private BartenderParams buildBartenderParams(WorkflowBartenderChatReq bartenderInput) {
        List<Bar> userBars = barService.getUserBarByBarIds(bartenderInput.getBarIds());
        String userStock = buildBarDescription(userBars);
        return BartenderParams.builder()
                .userStock(userStock)
                .userDemand(bartenderInput.getUserDemand())
                .theme(bartenderInput.getTheme())
                .themeFormula(bartenderInput.getThemeFormula())
                .build();
    }

    private ChatParams buildChatParams(List<Bar> bars) {
        ChatParams chatParams = new ChatParams();
        chatParams.setUserStock(buildBarDescription(bars));
        return chatParams;
    }

    private String buildBarDescription(List<Bar> bars) {
        if ( CollectionUtils.isEmpty(bars)) {
            return "null";
        }
        return bars.stream()
                .map(Bar::getDescription)
                .collect(Collectors.joining("\n"));
    }
}
