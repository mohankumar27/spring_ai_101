package com.mohankumar27.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ManualRagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    // We define a template that expects 'context' and 'question'
    @Value("classpath:/prompts/rag-prompt.st")
    private Resource ragPromptTemplate;

    public ManualRagService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    public String chat(String question) {
        // 1. Retrieve: Find similar documents
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(2)
                .build();
        List<Document> similarDocuments = vectorStore.similaritySearch(request);
        // 2. Augment: Convert documents to a single string
        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
        // 3. Generate: Create prompt and call LLM
        PromptTemplate template = new PromptTemplate(ragPromptTemplate);
        Prompt prompt = template.create(Map.of(
                "context", context,
                "question", question
        ));
        return chatClient.prompt(prompt).call().content();
    }
}
