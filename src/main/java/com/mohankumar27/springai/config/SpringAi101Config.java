package com.mohankumar27.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAi101Config {

    @Bean
    ChatClient travelChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a friendly travel agent. answer in a concise, professional tone.")
                .build();
    }
}
