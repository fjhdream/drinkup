package cool.drinkup.drinkup.workflow.service.chat;

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
import java.util.stream.Collectors;

import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserChatReq.WorkflowUserChatVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatBotService {

    private final ChatModel chatModel;
    private final String promptTemplate;

    @Value("${drinkup.chat.model}")
    private String model;

    public ChatBotService(@Qualifier("openAiChatModel") ChatModel chatModel, ResourceLoader resourceLoader) throws IOException {
        this.chatModel = chatModel;
        Resource promptResource = resourceLoader.getResource("classpath:prompts/chat-prompt.txt");
        this.promptTemplate = new String(promptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public String chat(List<WorkflowUserChatVo> messages) {
        var prompt = buildPrompt(messages);
        var response = chatModel.call(prompt);
        String text = response.getResult().getOutput().getText();
        log.info("Chat response: {}", text);
        return text;
    }

    private Prompt buildPrompt(List<WorkflowUserChatVo> messages) {
        String systemPrompt = promptTemplate.replace("{userStock}", "");

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

        return new Prompt(allMessages, OpenAiChatOptions.builder()
                    .model(this.model)
                    .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.JSON_OBJECT).build())
                    .build());
    }
}
