package cool.drinkup.drinkup.workflow.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import cool.drinkup.drinkup.workflow.internal.model.BarStock;

public interface BarStockRepository extends JpaRepository<BarStock, Long> {

    List<BarStock> findByBarId(Long barId);
    
    Optional<BarStock> findByIdAndBarId(Long id, Long barId);

}
