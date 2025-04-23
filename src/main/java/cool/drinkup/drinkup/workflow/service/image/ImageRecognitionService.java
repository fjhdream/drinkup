package cool.drinkup.drinkup.workflow.service.image;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cool.drinkup.drinkup.workflow.controller.req.BarStockCreateReq;
import cool.drinkup.drinkup.workflow.controller.req.BarStockCreateReq.InnerBarStockCreateReq;
import cool.drinkup.drinkup.workflow.model.BarStock;
import cool.drinkup.drinkup.workflow.util.ContentTypeUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageRecognitionService {
    @Qualifier("openAiChatModel")
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final ImageService imageService;
    private final ContentTypeUtil contentTypeUtil;
    private String promptTemplate;

    @Value("${drinkup.image.recognition.model:google/gemini-2.0-flash-001}")
    private String model;

    public ImageRecognitionService(ResourceLoader resourceLoader, @Qualifier("openAiChatModel") ChatModel chatModel,
            ObjectMapper objectMapper, ImageService imageService, ContentTypeUtil contentTypeUtil) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
        this.imageService = imageService;
        this.contentTypeUtil = contentTypeUtil;
        try {
            Resource promptResource = resourceLoader.getResource("classpath:prompts/image-recognition-prompt.txt");
            this.promptTemplate = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error loading image recognition prompt template", e);
            this.promptTemplate = "You are an AI assistant that recognizes alcoholic beverages and bar ingredients from images. "
                    +
                    "Identify all visible bottles, ingredients, and bar items in the image. " +
                    "For each item, provide the name, type (spirit, liqueur, mixer, etc.), and a brief description.";
        }
    }

    public List<BarStock> recognizeStockFromImage(String imageId) {
        try {

            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(promptTemplate));
            Resource image = imageService.loadImage(imageId);
            String mimeType = contentTypeUtil.detectMimeType(image).toString();
            messages.add(new UserMessage("请识别图片中的所有物品，并返回物品的名称、类型和描述。",
                    new Media(MimeType.valueOf(mimeType), image)));
            Prompt prompt = new Prompt(messages);
            ChatResponse call = chatModel.call(prompt);
            Generation result = call.getResult();
            String response = result.getOutput().getText();
            log.info("Image recognition response: {}", response);
            return parseRecognitionResponse(response);
        } catch (Exception e) {
            log.error("Error processing image for stock recognition", e);
            return new ArrayList<>();
        }
    }

    private List<BarStock> parseRecognitionResponse(String response) {
        try {
            // Extract JSON array from response if needed
            String jsonContent = extractJsonContent(response);

            BarStockCreateReq barStockCreateReq = objectMapper.readValue(jsonContent, BarStockCreateReq.class);

            // Parse the JSON array into a list of InnerBarStockCreateReq objects
            List<InnerBarStockCreateReq> stockItems = barStockCreateReq.getBarStocks() == null ? new ArrayList<>()
                    : barStockCreateReq.getBarStocks();

            // Convert to BarStock objects
            List<BarStock> barStocks = new ArrayList<>();
            for (InnerBarStockCreateReq item : stockItems) {
                BarStock stock = new BarStock();
                stock.setName(item.getName());
                stock.setType(item.getType());
                stock.setIconType(item.getIconType());
                stock.setDescription(item.getDescription());
                barStocks.add(stock);
            }

            return barStocks;
        } catch (JsonProcessingException e) {
            log.error("Error parsing recognition response", e);
            return new ArrayList<>();
        }
    }

    private String extractJsonContent(String response) {
        // If response contains JSON array directly
        if (response.trim().startsWith("[") && response.trim().endsWith("]")) {
            return response.trim();
        }

        // If JSON is wrapped in code blocks
        if (response.contains("```json") && response.contains("```")) {
            int start = response.indexOf("```json") + 7;
            int end = response.indexOf("```", start);
            return response.substring(start, end).trim();
        }

        // If no JSON format is found, return empty array
        return "[]";
    }
}