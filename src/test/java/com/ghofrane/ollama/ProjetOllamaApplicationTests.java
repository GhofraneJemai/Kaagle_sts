package com.ghofrane.ollama;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@SpringBootTest
class ProjetOllamaApplicationTests {

    @Autowired(required = false)
    private EmbeddingModel embeddingModel;

    @Autowired(required = false)
    private VectorStore vectorStore;

    @Test
    void testPdfReading() throws Exception {
        Resource pdfResource = new ClassPathResource("/pdfs/cv.pdf");
        System.out.println("PDF exists? " + pdfResource.exists());
        
        if (pdfResource.exists()) {
            System.out.println("PDF path: " + pdfResource.getFile().getAbsolutePath());
        } else {
            System.out.println("PDF NOT FOUND! Check path: classpath:/pdfs/cv.pdf");
            return;
        }

        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);
        List<Document> documents = reader.get();
        System.out.println("Nombre de pages extraites: " + documents.size());

        // Print content of each page before splitting
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            System.out.println("=== Page " + (i + 1) + " ===");
            System.out.println("Content: " + doc.getFormattedContent());
            System.out.println("Metadata: " + doc.getMetadata());
            System.out.println("=====================");
        }

        TextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);
        System.out.println("Nombre de chunks générés: " + chunks.size());

        // Afficher le contenu du premier chunk
        if (!chunks.isEmpty()) {
            Document firstChunk = chunks.get(0);
            System.out.println("Premier chunk: " + firstChunk.getFormattedContent());
            System.out.println("Metadata du premier chunk: " + firstChunk.getMetadata());
        }
    }

    @Test
    void testEmbeddingModel() {
        System.out.println("=== TEST EMBEDDING MODEL ===");
        if (embeddingModel == null) {
            System.out.println("❌ EmbeddingModel NOT FOUND - Check configuration");
            return;
        }

        try {
            // Test avec une petite phrase - utilise float[] au lieu de List<Double>
            float[] embedding = embeddingModel.embed("test embedding");
            System.out.println("✅ Embedding model is working!");
            System.out.println("Embedding vector size: " + embedding.length);
            System.out.println("First 5 values: ");
            for (int i = 0; i < Math.min(5, embedding.length); i++) {
                System.out.println("  [" + i + "]: " + embedding[i]);
            }
        } catch (Exception e) {
            System.out.println("❌ Error with embedding model: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void testVectorStoreWithQuery() {
        System.out.println("=== TEST VECTOR STORE WITH QUERY ===");
        if (vectorStore == null) {
            System.out.println("❌ VectorStore NOT FOUND - Check configuration");
            return;
        }

        // Test avec différentes requêtes
        String[] testQueries = {
            "profil professionnel",
            "expérience",
            "compétences",
            "formation",
            "curriculum vitae"
        };

        for (String query : testQueries) {
            System.out.println("\n--- Testing query: '" + query + "' ---");
            try {
                List<Document> results = vectorStore.similaritySearch(query);
                System.out.println("Found " + results.size() + " documents");

                for (int i = 0; i < results.size(); i++) {
                    Document doc = results.get(i);
                    System.out.println("Document " + (i + 1) + ":");
                    System.out.println("Content preview: " + 
                        (doc.getFormattedContent().length() > 100 ? 
                         doc.getFormattedContent().substring(0, 100) + "..." : 
                         doc.getFormattedContent()));
                    System.out.println("Metadata: " + doc.getMetadata());
                    System.out.println("---");
                }
            } catch (Exception e) {
                System.out.println("❌ Error searching for query '" + query + "': " + e.getMessage());
            }
        }
    }

    @Test
    void testCompleteRagPipeline() {
        System.out.println("=== TEST COMPLETE RAG PIPELINE ===");
        
        // Test de lecture PDF
        try {
            Resource pdfResource = new ClassPathResource("/pdfs/cv.pdf");
            if (!pdfResource.exists()) {
                System.out.println("❌ PDF file not found!");
                return;
            }

            PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);
            List<Document> documents = reader.get();
            System.out.println("✅ PDF reading successful: " + documents.size() + " pages");

            // Test d'embedding - utilise float[] au lieu de List<Double>
            if (embeddingModel != null) {
                float[] embedding = embeddingModel.embed("test");
                System.out.println("✅ Embedding model working: vector size " + embedding.length);
            }

            // Test vector store
            if (vectorStore != null) {
                List<Document> results = vectorStore.similaritySearch("profil");
                System.out.println("✅ Vector store working: found " + results.size() + " documents for 'profil'");
                
                // Afficher un extrait des résultats
                if (!results.isEmpty()) {
                    System.out.println("Sample content from vector store:");
                    String content = results.get(0).getFormattedContent();
                    System.out.println(content.length() > 200 ? content.substring(0, 200) + "..." : content);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error in RAG pipeline: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void testSpecificQueries() {
        System.out.println("=== TEST SPECIFIC QUERIES FOR CV ===");
        
        if (vectorStore == null) {
            System.out.println("VectorStore not available");
            return;
        }

        // Requêtes spécifiques pour un CV
        String[] cvQueries = {
            "Quel est le profil professionnel?",
            "Quelles sont les compétences techniques?",
            "Quelle est l'expérience professionnelle?",
            "Quelles sont les formations?",
            "Quels sont les langages de programmation?",
            "Combien d'années d'expérience?"
        };

        for (String query : cvQueries) {
            System.out.println("\n🔍 Query: " + query);
            try {
                List<Document> results = vectorStore.similaritySearch(query);
                System.out.println("📄 Documents found: " + results.size());
                
                for (int i = 0; i < Math.min(results.size(), 2); i++) { // Limiter à 2 premiers résultats
                    Document doc = results.get(i);
                    String content = doc.getFormattedContent();
                    System.out.println("📝 Document " + (i + 1) + " (length: " + content.length() + " chars):");
                    System.out.println("Preview: " + (content.length() > 150 ? content.substring(0, 150) + "..." : content));
                    System.out.println("---");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    @Test
    void testEmbeddingForMultipleTexts() {
        System.out.println("=== TEST EMBEDDING FOR MULTIPLE TEXTS ===");
        if (embeddingModel == null) {
            System.out.println("EmbeddingModel not available");
            return;
        }

        try {
            // Test avec plusieurs textes
            List<String> texts = List.of(
                "profil professionnel",
                "expérience en développement",
                "compétences techniques"
            );

            // Utilise embed(List<String>) qui retourne List<float[]>
            List<float[]> embeddings = embeddingModel.embed(texts);
            System.out.println("✅ Multiple embeddings generated: " + embeddings.size());
            
            for (int i = 0; i < embeddings.size(); i++) {
                System.out.println("Text: '" + texts.get(i) + "' -> Embedding size: " + embeddings.get(i).length);
            }
        } catch (Exception e) {
            System.out.println("❌ Error generating multiple embeddings: " + e.getMessage());
            e.printStackTrace();
        }
    }
}