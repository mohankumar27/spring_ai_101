package com.mohankumar27.springai.basics;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class SimpleChatController {
    private final ChatClient chatClient;
    // Spring AI auto-configures the ChatClient.Builder
    public SimpleChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/simple")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
