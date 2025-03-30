package cool.drinkup.drinkup.workflow.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import cool.drinkup.drinkup.workflow.controller.req.WorkflowUserReq;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWIneResp;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.workflow.mapper.WineMapper;
import cool.drinkup.drinkup.workflow.model.Wine;
import cool.drinkup.drinkup.workflow.respository.WineRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkflowService {

    private final VectorStore vectorStore;

    private final WineRepository wineRepository;

    private final WineMapper wineMapper;

    public WorkflowService(VectorStore vectorStore, WineRepository wineRepository, WineMapper wineMapper) {
        this.vectorStore = vectorStore;
        this.wineRepository = wineRepository;
        this.wineMapper = wineMapper;
    }

    public WorkflowUserWIneResp processCocktailRequest(WorkflowUserReq userInput) {
        String userInputText = userInput.getUserInput();
        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder().query(userInputText).topK(2).build());
        log.info("Results: {}", results);
        List<Long> wineIds = results.stream()
                .map(Document::getMetadata)
                .map(metadata -> Long.valueOf(metadata.get("wineId").toString()))
                .collect(Collectors.toList());
        List<Wine> wines = wineRepository.findAllById(wineIds);
        log.info("Wines: {}", wines);
        List<WorkflowUserWineVo> workflowUserWineVos = wines.stream()
                .map(wineMapper::toWineVo)
                .collect(Collectors.toList());
        WorkflowUserWIneResp workflowUserWIneResp = new WorkflowUserWIneResp();
        workflowUserWIneResp.setWines(workflowUserWineVos);
        return workflowUserWIneResp;
    }
}
