package cool.drinkup.drinkup.workflow.model;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "bar")
@Data
public class Bar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;

    @OneToMany(mappedBy = "barId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BarStock> barStocks;


    public String getDescription() {
        StringBuilder description = new StringBuilder();
        description.append("Bar Name: ").append(name).append("\n");
        description.append("Bar Stocks: ").append(barStocks.stream().map(BarStock::getDescription).collect(Collectors.joining(", ")));
        return description.toString();
    }
}