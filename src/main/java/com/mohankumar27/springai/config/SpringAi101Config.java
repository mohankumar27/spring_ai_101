package com.mohankumar27.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
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

    @Bean
    EmbeddingModel embeddingModel() {
        return new TransformersEmbeddingModel(); // Runs locally, no Key needed. If you plan to use OpenAI embedding model from library, remove this bean
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // Spring AI automatically configures the OpenAI EmbeddingModel
        // We pass it to the store so it can calculate embeddings
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
