package cool.drinkup.drinkup.workflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "bar_stock")
@Data
public class BarStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long barId;

    private String name;

    private String type;

    public String getDescription() {
        return "[库存类型: " + type + " 库存名称: " + name + "]";
    }
}