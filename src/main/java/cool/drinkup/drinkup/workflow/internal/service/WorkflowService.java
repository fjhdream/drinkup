package cool.drinkup.drinkup.workflow.internal.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cool.drinkup.drinkup.infrastructure.spi.ImageGenerator;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowBartenderChatReq;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowStockRecognitionReq;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowUserChatReq;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowUserReq;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowBartenderChatResp;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowStockRecognitionResp;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowUserChatResp;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowWineResp;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowWineVo;
import cool.drinkup.drinkup.workflow.internal.mapper.WineMapper;
import cool.drinkup.drinkup.workflow.internal.model.Bar;
import cool.drinkup.drinkup.workflow.internal.model.BarStock;
import cool.drinkup.drinkup.workflow.internal.model.Wine;
import cool.drinkup.drinkup.workflow.internal.repository.WineRepository;
import cool.drinkup.drinkup.workflow.internal.service.bar.BarService;
import cool.drinkup.drinkup.workflow.internal.service.bartender.BartenderService;
import cool.drinkup.drinkup.workflow.internal.service.bartender.dto.BartenderParams;
import cool.drinkup.drinkup.workflow.internal.service.bartender.theme.Theme;
import cool.drinkup.drinkup.workflow.internal.service.bartender.theme.ThemeEnum;
import cool.drinkup.drinkup.workflow.internal.service.bartender.theme.ThemeFactory;
import cool.drinkup.drinkup.workflow.internal.service.chat.ChatBotService;
import cool.drinkup.drinkup.workflow.internal.service.chat.dto.ChatParams;
import cool.drinkup.drinkup.workflow.internal.service.image.ImageRecognitionService;
import cool.drinkup.drinkup.workflow.internal.service.image.ImageService;
import cool.drinkup.drinkup.workflow.internal.service.stock.BarStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final VectorStore vectorStore;
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;
    private final UserWineService userWineService;
    private final ChatBotService chatBotService;
    private final BartenderService bartenderService;
    private final BarStockService barStockService;
    private final BarService barService;
    private final ObjectMapper objectMapper;
    private final ImageRecognitionService imageRecognitionService;
    private final ImageService imageService;
    private final ThemeFactory themeFactory;
    private final ImageGenerator imageGenerator;

    public WorkflowWineResp processCocktailRequest(WorkflowUserReq userInput) {
        String userInputText = userInput.getUserInput();
        List<Document> results = vectorStore
                .similaritySearch(SearchRequest.builder().query(userInputText).topK(10).build());
        log.info("Results: {}", results);
        List<Long> wineIds = results.stream()
                .map(Document::getMetadata)
                .map(metadata -> Long.valueOf(metadata.get("wineId").toString()))
                .collect(Collectors.toList());
        List<Wine> wines = wineRepository.findAllById(wineIds);
        log.info("Wines: {}", wines);
        List<WorkflowWineVo> workflowUserWineVos = wines.stream()
                .map(wineMapper::toWineVo)
                .collect(Collectors.toList());
        WorkflowWineResp workflowUserWIneResp = new WorkflowWineResp();
        workflowUserWIneResp.setWines(workflowUserWineVos);
        return workflowUserWIneResp;
    }

    public WorkflowUserChatResp chat(WorkflowUserChatReq userInput) {
        List<Bar> bars = barService.getUserBarByBarIds(userInput.getBarIds());
        ChatParams chatParams = buildChatParams(bars, userInput.getImageId());
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
        ChatParams chatParams = buildChatParams(bars, userInput.getImageId());
        return chatBotService.chatStreamFlux(userInput.getMessages(), chatParams);
    }

    private String extractJson(String chatWithUser) {
        if ( !chatWithUser.contains("```json")) {
            return chatWithUser;
        }
        // Extract JSON content between ```json and ``` markers
        String jsonPattern = "```json\s*(.*?)\s*```";
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
            String imageUrl = imageGenerator.generateImage(chatBotResponse.getImagePrompt());
            String imageId = imageService.storeImage(imageUrl);
            chatBotResponse.setImage(imageId);
            userWineService.saveUserWine(chatBotResponse);
            chatBotResponse.setImage(imageService.getImageUrl(imageId));
            return chatBotResponse;
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            return null;
        }
    }

    private BartenderParams buildBartenderParams(WorkflowBartenderChatReq bartenderInput) {
        List<Bar> userBars = barService.getUserBarByBarIds(bartenderInput.getBarIds());
        String userStock = buildBarDescription(userBars);
        Theme theme = themeFactory.getTheme(ThemeEnum.fromValue(bartenderInput.getTheme()));
        return BartenderParams.builder()
                .userStock(userStock)
                .userDemand(bartenderInput.getUserDemand())
                .theme(theme.getName())
                .build();
    }

    private ChatParams buildChatParams(List<Bar> bars, String imageId) {
        ChatParams chatParams = new ChatParams();
        chatParams.setUserStock(buildBarDescription(bars));
        chatParams.setImageId(StringUtils.hasText(imageId) ? imageId : null);
        return chatParams;
    }

    private String buildBarDescription(List<Bar> bars) {
        if ( CollectionUtils.isEmpty(bars)) {
            return "null";
        }
        return bars.stream()
                .map(Bar::getBarDescription)
                .collect(Collectors.joining("\n"));
    }

    public WorkflowStockRecognitionResp recognizeStock(WorkflowStockRecognitionReq req) {
        try {
            // 使用图像识别服务识别库存
            List<BarStock> recognizedStocks = imageRecognitionService.recognizeStockFromImage(req.getImageId());

            Bar bar = new Bar();
            bar.setId(req.getBarId());
            // 设置barId
            recognizedStocks.forEach(stock -> stock.setBar(bar));

            // 保存识别的库存到数据库
            List<BarStock> savedStocks = barStockService.saveAll(recognizedStocks);

            // 构建响应
            WorkflowStockRecognitionResp resp = new WorkflowStockRecognitionResp();
            resp.setBarId(req.getBarId());
            resp.setRecognizedStocks(savedStocks);

            return resp;
        } catch (Exception e) {
            log.error("Error recognizing stock from image", e);
            return null;
        }
    }
}
