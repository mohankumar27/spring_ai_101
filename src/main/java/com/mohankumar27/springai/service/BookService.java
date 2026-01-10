package com.mohankumar27.springai.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    // Simulating a database lookup
    @Tool(description = "Get the number of copies available for a specific book title")
    public int getBookInventory(@ToolParam(description = "The exact title of the book") String title) {
        System.out.println("AI requested inventory for: " + title);
        if ("Spring AI in Action".equalsIgnoreCase(title)) {
            return 5;
        }
        return 0;
    }

    @Tool(description = "Reserve a book for a customer")
    public String reserveBook(String title, String customerName) {
        return "Reserved " + title + " for " + customerName;
    }

    @Tool(description = "Search documentation", returnDirect = true)
    public String searchDocs(String query) {
        // Simulate a documentation search
        return "Found documentation for query: " + query;
    }
}
