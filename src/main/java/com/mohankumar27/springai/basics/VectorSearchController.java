package com.mohankumar27.springai.basics;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
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
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .build()
        );
        return results.stream()
                .map(Document::getText)
                .toList();
    }

    @GetMapping("/advanced")
    public List<String> searchAdvanced(@RequestParam String query) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(2) // Return the top 2 results
                        .similarityThreshold(0.7) // Only return results with >70% match
                        .build()
        );
        return results.stream()
                .map(Document::getText)
                .toList();
    }

    @GetMapping("/filtered")
    public List<String> filteredSearch(@RequestParam String query, @RequestParam int year, @RequestParam String dept) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        // specific to your metadata keys
        var expression = b.and(
                b.eq("year", year),
                b.eq("department", dept)
        ).build();
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .filterExpression(expression)
                .build();
        List<Document> results = vectorStore.similaritySearch(request);

        return results.stream()
                .map(Document::getText)
                .toList();
    }
}
