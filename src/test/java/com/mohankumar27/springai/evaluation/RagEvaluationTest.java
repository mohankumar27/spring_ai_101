package com.mohankumar27.springai.evaluation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RagEvaluationTest {

    private final ChatClient chatClient;
    private final ChatClient vectorStoreChatClient;

    @Autowired
    public RagEvaluationTest(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.clone()
                        .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.vectorStoreChatClient = builder.clone()
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(2).build())
                                .build()
                )
                .build();
    }

    @Test
    void testResponseRelevancy() {
        // 1. Setup the Evaluator
        RelevancyEvaluator evaluator = new RelevancyEvaluator(chatClient.mutate());
        // 2. Ask the RAG system a question
        String query = "Code of conduct";
        String answer = chatClient.prompt(query).call().content();

        // 3. Evaluate
        EvaluationRequest request = new EvaluationRequest(query, Collections.emptyList(), answer);
        EvaluationResponse evaluation = evaluator.evaluate(request);
        // 4. Assert
        assertTrue(evaluation.isPass(), "Response was not relevant to the query");
    }

    @Test
    void testHallucinations() {
        // 1. Setup the Evaluator
        FactCheckingEvaluator evaluator = FactCheckingEvaluator.builder(vectorStoreChatClient.mutate()).build();
        // 2. Perform RAG generation
        String query = "Remote work policy";
        ChatResponse response = vectorStoreChatClient.prompt(query).call().chatResponse();
        Assertions.assertNotNull(response);
        String answer = response.getResult().getOutput().getText();

        // 3. Extract the context documents used by the Advisor
        List<Document> retrievedDocs = response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        if (retrievedDocs == null) {
            retrievedDocs = Collections.emptyList();
            System.err.println("WARNING: No documents found in response metadata. RAG evaluation may be inaccurate.");
        }
        // 4. Evaluate: Does the context support the answer?
        EvaluationRequest request = new EvaluationRequest(query, retrievedDocs, answer);
        EvaluationResponse evaluation = evaluator.evaluate(request);

        // 5. Assert
        assertTrue(evaluation.isPass(), "The model hallucinated info not found in documents");
    }
}
