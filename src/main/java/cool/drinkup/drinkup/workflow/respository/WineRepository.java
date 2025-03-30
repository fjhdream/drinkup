package cool.drinkup.drinkup.workflow.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import cool.drinkup.drinkup.workflow.model.Wine;

public interface WineRepository extends JpaRepository<Wine, Long> {
    
}
