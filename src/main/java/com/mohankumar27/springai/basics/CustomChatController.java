package com.mohankumar27.springai.basics;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/custom")
public class CustomChatController {

    private final ChatClient travelChatClient;

    public CustomChatController(ChatClient travelChatClient) {
        this.travelChatClient =  travelChatClient;
    }

    @PostMapping("/top-destinations")
    public String getTopDestinations(@RequestBody String city) {
        return this.travelChatClient.prompt()
                .user(u -> u.text("I am visiting {city}. What are the top 3 destination places?")
                        .param("city", city))
                .call()
                .content();
    }

    @GetMapping("/creative-story")
    public String getCreativeStory() {
        return this.travelChatClient.prompt()
                .user("Can you tell me a creative story about a traveling dragon?")
                .options(ChatOptions.builder()
                        .temperature(0.8) // Higher temperature for more creativity
                        .build())
                .call()
                .content();
    }

    @GetMapping(value = "/stream-story", produces = "text/event-stream")
    public Flux<String> streamCreativeStory() {
        return this.travelChatClient.prompt()
                .user("Can you tell me a creative story about a traveling dragon?")
                .options(OpenAiChatOptions.builder()
                        .temperature(0.8) // Higher temperature for more creativity)
                        .build())
                .stream()
                .content();
    }
}
