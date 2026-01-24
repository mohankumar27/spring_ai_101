package com.mohankumar27.springai.advisors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

import java.util.Random;

public class SelfCorrectionAdvisor implements CallAdvisor {
    private static final Logger log = LoggerFactory.getLogger(SelfCorrectionAdvisor.class);
    private final int maxRetries;

    public SelfCorrectionAdvisor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {

        // 1. Initial Call: Send request to the LLM
        ChatClientResponse response = chain.nextCall(request);
        int attempt = 0;
        // 2. Evaluation Loop: Check if the response meets criteria
        while (!isValid(response) && attempt < maxRetries) {
            attempt++;
            // 3. Construct Feedback: Create a new prompt including the error
            String feedback = "The previous json response is invalid. Please try again.";
            ChatClientRequest retryRequest = request.mutate()
                    .prompt(request.prompt().augmentUserMessage(feedback))
                    .build();
            // 4. Recursive Call: Re-invoke the chain with the new request
            // chain.copy(this) ensures we only re-execute downstream advisors/model
            response = chain.copy(this).nextCall(retryRequest);
        }
        return response;
    }

    private boolean isValid(ChatClientResponse response) {
        // Implement validation logic (e.g., check JSON schema, verify facts, etc.)
        boolean val = new Random().nextBoolean();
        log.info("SelfCorrectionAdvisor:: isValid={}", val);
        return val;
    }

    @Override
    public int getOrder() {
        return 0; // High priority to wrap other advisors
    }

    @Override
    public String getName() {
        return "SelfCorrectionAdvisor";
    }
}
