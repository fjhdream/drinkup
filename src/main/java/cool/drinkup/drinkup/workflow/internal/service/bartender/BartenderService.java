package cool.drinkup.drinkup.workflow.internal.service.bartender;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cool.drinkup.drinkup.workflow.internal.config.BartenderProperties;
import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowBartenderChatReq.WorkflowBartenderChatVo;
import cool.drinkup.drinkup.workflow.internal.exception.RetryException;
import cool.drinkup.drinkup.workflow.internal.service.bartender.dto.BartenderParams;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BartenderService {

    private final ChatModel chatModel;
    private final ChatModel recoverableChatModel;
    private final String promptTemplate;
    private final BartenderProperties bartenderProperties;

    public BartenderService(@Qualifier("bartenderChatModel") ChatModel chatModel, @Qualifier("bartenderRecoverableChatModel") ChatModel recoverableChatModel, ResourceLoader resourceLoader, BartenderProperties bartenderProperties)
            throws IOException {
        this.chatModel = chatModel;
        this.recoverableChatModel = recoverableChatModel;
        Resource promptResource = resourceLoader.getResource("classpath:prompts/bartender-prompt.txt");
        this.promptTemplate = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        this.bartenderProperties = bartenderProperties;
    }

    @Retryable(
        value = {RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    @Observed(name = "ai.bartender.chat",
        contextualName = "Bartender聊天",
        lowCardinalityKeyValues = {
            "Tag", "ai"
        })
    public String generateDrink(List<WorkflowBartenderChatVo> messages, BartenderParams bartenderParams) {
        try {
            var prompt = buildPrompt(messages, bartenderParams);
            var response = chatModel.call(prompt);
            log.info("bartender response: {}", response);
            
            if (response.getResult() == null) {
                log.error("AI response result is null. Response details: {}", response);
                throw new RuntimeException("AI response result is null");
            }
            
            String text = response.getResult().getOutput().getText();
            log.info("Chat response: {}", text);
            return text;
        } catch (Exception e) {
            log.error("Error generating drink recommendation", e);
            throw new RetryException("Error generating drink recommendation");
        }
    }

    @Recover
    @Observed(name = "ai.bartender.chat",
        contextualName = "Bartender聊天重试",
        lowCardinalityKeyValues = {
            "Tag", "ai"
        })
    public String generateDrinkRecoverable(RetryException exception, List<WorkflowBartenderChatVo> messages, BartenderParams bartenderParams) {
        try {
            var prompt = buildPrompt(messages, bartenderParams);
            var response = recoverableChatModel.call(prompt);
            log.info("bartender response: {}", response);
            
            if (response.getResult() == null) {
                log.error("AI response result is null. Response details: {}", response);
                throw new RuntimeException("AI response result is null");
            }
            
            String text = response.getResult().getOutput().getText();
            log.info("Chat response: {}", text);
            return text;
        } catch (Exception e) {
            log.error("Error generating drink recommendation", e);
            throw new RuntimeException("Error generating drink recommendation after retry");
        }
    }

    private Prompt buildPrompt(List<WorkflowBartenderChatVo> messages, BartenderParams bartenderParams) {
        Map<String,String> substituterMap = bartenderParams.toSubstituterMap();
        StringSubstitutor substitutor = new StringSubstitutor(substituterMap);
        String systemPrompt = substitutor.replace(promptTemplate);

        var systemMessage = new SystemMessage(systemPrompt);
        var historyMessages = messages.stream()
                .map(message -> {
                    if ( message.getRole().equals("user")) {
                        return new UserMessage(message.getContent());
                    } else if ( message.getRole().equals("assistant")) {
                        return new AssistantMessage(message.getContent());
                    } else {
                        throw new IllegalArgumentException("Invalid message role: " + message.getRole());
                    }
                })
                .toList();

        var allMessages = new ArrayList<Message>();
        allMessages.add(systemMessage);
        allMessages.addAll(historyMessages);

        return new Prompt(allMessages, OpenAiChatOptions.builder()
                .model(bartenderProperties.getModel())
                .temperature(bartenderProperties.getTemperature())
                .responseFormat(
                    ResponseFormat
                    .builder()
                    .type(ResponseFormat.Type.JSON_OBJECT)
                    .build())
                .build());
    }

}
