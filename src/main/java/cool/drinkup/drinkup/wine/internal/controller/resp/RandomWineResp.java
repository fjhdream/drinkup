package cool.drinkup.drinkup.wine.internal.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RandomWineResp {
    private String type;
    private Object wine;
} 