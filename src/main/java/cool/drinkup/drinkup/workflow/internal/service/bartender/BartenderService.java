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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cool.drinkup.drinkup.workflow.internal.controller.req.WorkflowBartenderChatReq.WorkflowBartenderChatVo;
import cool.drinkup.drinkup.workflow.internal.service.bar.BarService;
import cool.drinkup.drinkup.workflow.internal.service.bartender.dto.BartenderParams;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BartenderService {

    private final ChatModel chatModel;
    private final String promptTemplate;

    @Value("${drinkup.bartender.model}")
    private String model;

    public BartenderService(@Qualifier("openAiChatModel") ChatModel chatModel, ResourceLoader resourceLoader)
            throws IOException {
        this.chatModel = chatModel;
        Resource promptResource = resourceLoader.getResource("classpath:prompts/bartender-prompt.txt");
        this.promptTemplate = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    @NewSpan
    public String generateDrink(List<WorkflowBartenderChatVo> messages, BartenderParams bartenderParams) {
        var prompt = buildPrompt(messages, bartenderParams);
        var response = chatModel.call(prompt);
        String text = response.getResult().getOutput().getText();
        log.info("Chat response: {}", text);
        return text;
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
                .model(this.model)
                .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.JSON_OBJECT).build())
                .build());
    }

}
