package cool.drinkup.drinkup.common.log.trace;

import com.fasterxml.jackson.databind.ObjectMapper;
import cool.drinkup.drinkup.common.log.repository.impl.LogRecord;
import cool.drinkup.drinkup.common.log.repository.impl.LogRecordElasticsearchRepository;
import cool.drinkup.drinkup.user.spi.AuthenticationServiceFacade;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class ElasticsearchTraceServiceImpl extends DefaultTraceServiceImpl {

    private final LogRecordElasticsearchRepository elasticsearchRepository;
    private final ObjectMapper objectMapper;

    public ElasticsearchTraceServiceImpl(
            AuthenticationServiceFacade authenticationServiceFacade,
            LogRecordElasticsearchRepository elasticsearchRepository,
            ObjectMapper objectMapper) {
        super(authenticationServiceFacade);
        this.elasticsearchRepository = elasticsearchRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void processTrace(Map<String, Object> traceData) {
        try {
            LogRecord logRecord = buildLogRecord(traceData);
            LogRecord saved = elasticsearchRepository.save(logRecord);

            log.info(
                    "Trace saved to Elasticsearch: id={}, type={}, subType={}, bizNo={}," + " operator={}",
                    saved.getId(),
                    saved.getType(),
                    saved.getSubType(),
                    saved.getBizNo(),
                    saved.getOperator());
        } catch (Exception e) {
            log.error("Failed to save trace to Elasticsearch: {}", traceData, e);
        }
    }

    private LogRecord buildLogRecord(Map<String, Object> traceData) {
        LogRecord.LogRecordBuilder builder = LogRecord.builder()
                .id(UUID.randomUUID().toString())
                .type((String) traceData.get("type"))
                .subType((String) traceData.get("subType"))
                .bizNo((String) traceData.get("bizNo"))
                .operator((String) traceData.get("operator"))
                .createTime(ZonedDateTime.now(ZoneOffset.UTC))
                .fail(false);

        String action = String.format("%s - %s", traceData.get("type"), traceData.get("subType"));
        builder.action(action);

        Object properties = traceData.get("properties");
        if (properties != null) {
            try {
                String extraJson = objectMapper.writeValueAsString(properties);
                builder.extra(extraJson);
            } catch (Exception e) {
                log.warn("Failed to serialize properties to JSON", e);
                builder.extra(properties.toString());
            }
        }

        Object timestamp = traceData.get("timestamp");
        if (timestamp != null) {
            try {
                if (timestamp instanceof ZonedDateTime) {
                    builder.createTime((ZonedDateTime) timestamp);
                } else if (timestamp instanceof String) {
                    builder.createTime(ZonedDateTime.parse(timestamp.toString()));
                }
            } catch (Exception e) {
                log.warn("Failed to parse timestamp, using current time", e);
            }
        }

        return builder.build();
    }
}
