package cool.drinkup.drinkup.workflow.service.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import cool.drinkup.drinkup.workflow.model.Wine;
import cool.drinkup.drinkup.workflow.repository.WineRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataLoaderService {

    private final EmbeddingModel embeddingModel;

    private final VectorStore vectorStore;

    private final WineRepository wineRepository;

    public DataLoaderService(@Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel, VectorStore vectorStore, WineRepository wineRepository) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
        this.wineRepository = wineRepository;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    public void loadData() {
        List<Wine> wines = wineRepository.findAllById(List.of(1L, 2L, 3L));
        for (Wine wine : wines) {
            String jsonString;
            try {
                jsonString = objectMapper.writeValueAsString(wine);
                Document document = new Document(jsonString, Map.of("wineId", wine.getId()));
                vectorStore.add(List.of(document));
            } catch (JsonProcessingException e) {
                log.error("Error converting wine to JSON string: {}", e.getMessage());
                continue;
            }
        }
    }
}
