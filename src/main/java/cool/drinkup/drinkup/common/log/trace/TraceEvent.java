package cool.drinkup.drinkup.common.log.trace;

import jakarta.validation.constraints.Pattern;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraceEvent implements TraceService.TraceEvent {

    @Pattern(regexp = "^[A-Z][A-Z_]*$", message = "Type must be uppercase letters and underscores only")
    private final String type;

    @Pattern(regexp = "^[A-Z][A-Z_]*$", message = "SubType must be uppercase letters and underscores only")
    private final String subType;

    private final String bizNo;

    private final Map<String, Object> properties;

    public static TraceEventBuilder builder() {
        return new TraceEventBuilder();
    }

    public static class TraceEventBuilder {
        private String type;
        private String subType;

        public TraceEventBuilder type(String type) {
            if (type != null && !type.matches("^[A-Z][A-Z_]*$")) {
                throw new IllegalArgumentException("Type must be uppercase letters and underscores only: " + type);
            }
            this.type = type;
            return this;
        }

        public TraceEventBuilder subType(String subType) {
            if (subType != null && !subType.matches("^[A-Z][A-Z_]*$")) {
                throw new IllegalArgumentException(
                        "SubType must be uppercase letters and underscores only: " + subType);
            }
            this.subType = subType;
            return this;
        }
    }
}
