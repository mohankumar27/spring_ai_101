package com.mohankumar27.springai.basics;

import com.mohankumar27.springai.service.ManualRagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
public class RagController {
    private final ManualRagService manualRagService;
    private final ChatClient chatClient;
    private final ChatClient customChatClient;

    public RagController(ChatClient.Builder builder, VectorStore vectorStore, ManualRagService manualRagService) {
        // We configure the advisor at build time
        this.chatClient = builder.clone()
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .build()
                )
                .build();

        this.manualRagService = manualRagService;

        String customTemplate = """
    You are a helpful assistant. Use the following context to answer the question.
    If the answer is not in the context, say "I do not know".
    CONTEXT:
    {question_answer_context}
    QUESTION:
    {query}
    """;
        this.customChatClient = builder.clone()
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5) // Retrieve top 5 documents
                                        .similarityThreshold(0.3) // Only matches with >30% similarity
                                        .filterExpression("year == '2025'") // Metadata filtering
                                        .build())
                                .promptTemplate(PromptTemplate.builder().template(customTemplate).build()) // Custom prompt
                                .build()
                )
                .build();
    }

    @GetMapping("/manualChat")
    public String manualChat(@RequestParam String query) {
        return manualRagService.chat(query);
    }

    @GetMapping("/advisorChat")
    public String advisorChat(@RequestParam String query) {
        return chatClient.prompt(query).call().content();
    }

    @GetMapping("/customAdvisorChat")
    public String customAdvisorChat(@RequestParam String query) {
        return customChatClient.prompt(query).call().content();
    }
}
