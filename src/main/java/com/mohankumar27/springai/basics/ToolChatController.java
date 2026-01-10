package com.mohankumar27.springai.basics;

import com.mohankumar27.springai.service.BookService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tools")
public class ToolChatController {
    private final ChatClient chatClient;
    private final BookService bookService;

    public ToolChatController(ChatClient.Builder chatClientBuilder, BookService bookService) {
        this.chatClient = chatClientBuilder.build();
        this.bookService = bookService;
    }

    @GetMapping("/book")
    public String chat(@RequestParam String message) {
        return this.chatClient.prompt()
                .user(message)
                .tools(bookService) // Register the bean containing @Tool methods
                .call()
                .content();
    }
}
