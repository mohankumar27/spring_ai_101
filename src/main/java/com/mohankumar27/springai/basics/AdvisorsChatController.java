package com.mohankumar27.springai.basics;

import com.mohankumar27.springai.advisors.ReReadingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/advisor")
public class AdvisorsChatController {

    private final ChatClient chatClient;
    private final ChatClient safeChatClient;

    public AdvisorsChatController(ChatClient.Builder chatClientBuilder) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory
        ).build(); // Adds conversational memory

        this.chatClient = chatClientBuilder.clone()
                .defaultAdvisors(new SimpleLoggerAdvisor(), // Adds logging to every call
                        messageChatMemoryAdvisor)
                .build();

        this.safeChatClient = chatClientBuilder.clone()
                .defaultAdvisors(new SimpleLoggerAdvisor(), // Adds logging to every call
                        SafeGuardAdvisor.builder()
                                .sensitiveWords(List.of("drugs", "medicine"))
                                .build() // Avoids replying to sensitive topics
                )
                .build();

    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, "user-123"))
                .user("My name is John")
                .call()
                .content();
    }

    @GetMapping("/chat2")
    public String chat2() {
        return chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, "user-123"))
                .user("What is my name?")
                .call()
                .content();
    }

    @GetMapping("/safeChat")
    public String safeChat(@RequestParam String topic) {
        return safeChatClient.prompt()
                .user("Find the latest news about " + topic + " and summarize it.")
                .call()
                .content();
    }

    @GetMapping("/custom")
    public String custom(@RequestParam String topic) {
        return chatClient.prompt()
                .advisors(new ReReadingAdvisor())
                .user("Find the latest news about " + topic + " and summarize it.")
                .call()
                .content();
    }
}
