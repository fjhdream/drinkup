package cool.drinkup.drinkup.common.log.trace;

import java.util.Map;

public interface TraceService {

    void trace(String type, String subType, String bizNo);

    void trace(String type, String subType, String bizNo, Map<String, Object> properties);

    void trace(TraceEvent event);

    interface TraceEvent {
        String getType();

        String getSubType();

        String getBizNo();

        Map<String, Object> getProperties();
    }
}
