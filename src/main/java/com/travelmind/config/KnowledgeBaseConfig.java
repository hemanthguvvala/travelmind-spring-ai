package com.travelmind.config;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds an in-memory vector store seeded with loyalty-policy documents.
 * Embeddings are computed by the local ONNX model (free, offline) at startup.
 */
@Configuration
public class KnowledgeBaseConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        store.add(List.of(
                new Document("Gold tier status requires earning 50,000 tier miles within a membership year."),
                new Document("Tier miles determine status; redeemable miles are spent on rewards. They are tracked separately."),
                new Document("Miles expire after 24 months of account inactivity."),
                new Document("EU261: for a flight cancellation notified less than 14 days before departure, compensation is 250-600 EUR depending on distance."),
                new Document("Gold members get two free checked bags and lounge access; Silver members get one free checked bag.")));
        return store;
    }
}
