package com.mohankumar27.springai.basics;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/mcp")
public class McpChatController {

    private final ChatClient chatClient;

    public McpChatController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpToolProvider) {
        Arrays.stream(mcpToolProvider.getToolCallbacks()).forEach(tool ->
                System.out.println("Registered MCP Tool: " + tool.getToolDefinition()));

        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(mcpToolProvider) // Registers MCP tools with the ChatClient
                .build();
    }

    @GetMapping("/search")
    public String research(@RequestParam String topic) {
        return chatClient.prompt()
                .user("Find the latest news about " + topic + " and summarize it. Make only one search tool call.")
                .call()
                .content();
    }
}
