package cool.drinkup.drinkup.workflow.internal.service.chat;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import cool.drinkup.drinkup.workflow.internal.config.ChatBotProperties;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowUserChatReq.WorkflowUserChatVo;
import cool.drinkup.drinkup.workflow.internal.service.chat.dto.ChatParams;
import cool.drinkup.drinkup.workflow.internal.service.image.ImageService;
import cool.drinkup.drinkup.workflow.internal.util.ContentTypeUtil;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class ChatBotService {

    private final ChatModel chatModel;
    private final String promptTemplate;
    private final ImageService imageService;
    private final ContentTypeUtil contentTypeUtil;
    private final ChatBotProperties chatBotProperties;

    public ChatBotService(@Qualifier("chatBotModel") ChatModel chatModel, ResourceLoader resourceLoader, 
    ImageService imageService, ContentTypeUtil contentTypeUtil, ChatBotProperties chatBotProperties) throws IOException {
        this.chatModel = chatModel;
        Resource promptResource = resourceLoader.getResource("classpath:prompts/chat-prompt.txt");
        this.promptTemplate = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        this.imageService = imageService;
        this.contentTypeUtil = contentTypeUtil;
        this.chatBotProperties = chatBotProperties;
    }

    @Observed(name = "ai.chat",
        contextualName = "AI聊天",
        lowCardinalityKeyValues = {
            "Tag", "ai"
        })
    public String chat(List<WorkflowUserChatVo> messages, ChatParams params) {
        var prompt = buildPrompt(messages, params);
        var response = chatModel.call(prompt);
        log.info("ai response : {}", response);
        String text = response.getResult().getOutput().getText();
        log.info("Chat response: {}", text);
        return text;
    }

    public CompletableFuture<Void> chatStream(List<WorkflowUserChatVo> messages, ChatParams params, Consumer<String> onChunk) {
        var prompt = buildPrompt(messages, params);
        Flux<ChatResponse> flux = chatModel.stream(prompt);
        
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        // Process each chunk as it arrives
        flux.subscribe(
            chunk -> {
                if (chunk.getResult() == null) {
                    log.debug("Received null result chunk, completing stream");
                    future.complete(null);
                    return;
                }
                String text = chunk.getResult().getOutput().getText();
                log.debug("Chat stream chunk: {}", text);
                onChunk.accept(text);
            },
            error -> {
                log.error("Error in chat stream: {}", error.getMessage());
                future.completeExceptionally(error);
            },
            () -> {
                log.debug("Chat stream completed");
                future.complete(null);
            }
        );
        
        return future;
    }

    public Flux<String> chatStreamFlux(List<WorkflowUserChatVo> messages, ChatParams params) {
        var prompt = buildPrompt(messages, params);
        return chatModel.stream(prompt)
            .map(response -> {
                if (response.getResult() == null) {
                    return "";
                }
                String text = response.getResult().getOutput().getText();
                log.debug("Chat stream chunk: {}", text);
                if (text == null) {
                    return "";
                }
                return text;
            })
            .filter(text -> !text.isEmpty());
    }

    private Prompt buildPrompt(List<WorkflowUserChatVo> messages, ChatParams params) {
        String systemPrompt = promptTemplate.replace("{userStock}", params.getUserStock());

        var systemMessage = new SystemMessage(systemPrompt);
        var historyMessages = messages.stream()
            .map(message -> {
                if (message.getRole().equals("user")) {
                    return new UserMessage(message.getContent());
                } else if (message.getRole().equals("assistant")) {
                    return new AssistantMessage(message.getContent());
                } else {
                    throw new IllegalArgumentException("Invalid message role: " + message.getRole());
                }
            })
            .collect(Collectors.toList());

        var allMessages = new ArrayList<Message>();
        allMessages.add(systemMessage);
        allMessages.addAll(historyMessages);
        if (params.getImageId() != null) {
            Resource resource = imageService.loadImage(params.getImageId());
            try {
                String mime = contentTypeUtil.detectMimeType(resource);
                Media media = new Media(MimeType.valueOf(mime), resource);
                UserMessage userMessage = UserMessage.builder().text("This is the image of the user's stock: ").media(List.of(media)).build();
                allMessages.add(userMessage);
            } catch (IOException e) {
                log.error("Error loading image: {}", e.getMessage());
            }
        }

        return new Prompt(allMessages, OpenAiChatOptions.builder()
                    .model(chatBotProperties.getModel())
                    .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.JSON_OBJECT).build())
                    .build());
    }
}
