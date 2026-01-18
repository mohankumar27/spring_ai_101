package com.mohankumar27.springai.advisors;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

public class ReReadingAdvisor implements CallAdvisor {

    private static final String RE2_TEMPLATE = """
        {input_query}
        Read the question again: {input_query}
        """;

    @Override
    public String getName() {
        return "ReReadingAdvisor";
    }

    @Override
    public int getOrder() {
        return 0; // High priority in the chain
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        // 1. Pre-process: Augment the user message
        String originalText = request.prompt().getUserMessage().getText();
        String augmentedText = RE2_TEMPLATE.replace("{input_query}", originalText);

        ChatClientRequest augmentedRequest = request.mutate()
                .prompt(request.prompt().augmentUserMessage(augmentedText))
                .build();

        // 2. Call the next entity in the chain
        ChatClientResponse response = chain.nextCall(augmentedRequest);

        // 3. Post-process: Return the response (can be modified if needed)
        return response;
    }
}
