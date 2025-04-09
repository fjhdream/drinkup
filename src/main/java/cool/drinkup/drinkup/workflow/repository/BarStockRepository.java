package cool.drinkup.drinkup.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import cool.drinkup.drinkup.workflow.model.BarStock;

public interface BarStockRepository extends JpaRepository<BarStock, Long> {

    List<BarStock> findByBarId(Long barId);

}
