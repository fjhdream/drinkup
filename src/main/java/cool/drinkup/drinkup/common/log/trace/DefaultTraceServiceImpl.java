package cool.drinkup.drinkup.common.log.trace;

import cool.drinkup.drinkup.user.spi.AuthenticatedUserDTO;
import cool.drinkup.drinkup.user.spi.AuthenticationServiceFacade;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultTraceServiceImpl implements TraceService {

    private final AuthenticationServiceFacade authenticationServiceFacade;

    @Override
    public void trace(String type, String subType, String bizNo) {
        trace(type, subType, bizNo, null);
    }

    @Override
    public void trace(String type, String subType, String bizNo, Map<String, Object> properties) {
        validateType(type);
        validateSubType(subType);

        TraceEvent event = cool.drinkup.drinkup.common.log.trace.TraceEvent.builder()
                .type(type)
                .subType(subType)
                .bizNo(bizNo)
                .properties(properties)
                .build();

        trace(event);
    }

    @Override
    public void trace(TraceEvent event) {
        String operator = getOperator();

        Map<String, Object> traceData = new HashMap<>();
        traceData.put("type", event.getType());
        traceData.put("subType", event.getSubType());
        traceData.put("bizNo", event.getBizNo());
        traceData.put("operator", operator);
        traceData.put("timestamp", LocalDateTime.now());

        if (event.getProperties() != null) {
            traceData.put("properties", event.getProperties());
        }

        log.info("TRACE: {}", traceData);

        processTrace(traceData);
    }

    private String getOperator() {
        Optional<AuthenticatedUserDTO> user = authenticationServiceFacade.getCurrentAuthenticatedUser();
        if (user.isPresent()) {
            return user.get().userId().toString();
        }
        return "ANONYMOUS";
    }

    private void validateType(String type) {
        if (type == null || !type.matches("^[A-Z][A-Z_]*$")) {
            throw new IllegalArgumentException("Type must be uppercase letters and underscores only: " + type);
        }
    }

    private void validateSubType(String subType) {
        if (subType == null || !subType.matches("^[A-Z][A-Z_]*$")) {
            throw new IllegalArgumentException("SubType must be uppercase letters and underscores only: " + subType);
        }
    }

    protected void processTrace(Map<String, Object> traceData) {
        // Override this method to implement custom trace processing
        // e.g., save to database, send to message queue, etc.
    }
}
