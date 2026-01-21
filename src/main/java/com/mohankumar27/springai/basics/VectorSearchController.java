package com.mohankumar27.springai.basics;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vector")
public class VectorSearchController {
    private final VectorStore vectorStore;

    public VectorSearchController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }
    @GetMapping("/search")
    public List<String> search(@RequestParam String query) {
        // Retrieve the top 2 most similar document chunks
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(2)
                        .build()
        );
        return results.stream()
                .map(Document::getText)
                .toList();
    }
}
