package cool.drinkup.drinkup.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cool.drinkup.drinkup.workflow.model.Wine;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {
    
}
