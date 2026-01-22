package com.mohankumar27.springai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngestionService implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;
    // Use the Value annotation to inject a document from resources
    @Value("classpath:docs/company-policy.txt")
    private Resource policyDocument;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingest() {
        // 1. Extract
        TextReader reader = new TextReader(policyDocument);
        reader.getCustomMetadata().put("filename", "company-policy.txt");
        List<Document> rawDocuments = reader.get();
        for(Document document : rawDocuments) {
            document.getMetadata().put("year", "2025");
            document.getMetadata().put("department", "HR");
        }
        log.info("Loaded {} document(s), metadata={}", rawDocuments.size(), rawDocuments.get(0).getMetadata());
        // 2. Transform (Split)
        // Split text into chunks of roughly 100 tokens to fit in context windows
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(100).build();
        List<Document> splitDocuments = splitter.apply(rawDocuments);
        log.info("Split into {} chunks, metadata={}", splitDocuments.size(), splitDocuments.get(0).getMetadata());
        // 3. Load (Write)
        // This line calls the EmbeddingModel to turn text into vectors
        // and stores them in the DB.
        vectorStore.add(splitDocuments);
        log.info("Ingestion complete!");
    }

    @Override
    public void run(String... args) throws Exception {
        ingest(); // Reads document and Tokenizes it into chunks when the application starts
    }
}
