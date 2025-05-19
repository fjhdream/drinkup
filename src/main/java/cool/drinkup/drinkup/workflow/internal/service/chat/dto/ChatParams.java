package cool.drinkup.drinkup.workflow.internal.service.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParams {
    private String userStock;
    private String imageId;
}
