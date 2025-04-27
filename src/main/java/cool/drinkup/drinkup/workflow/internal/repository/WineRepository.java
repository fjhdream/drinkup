package cool.drinkup.drinkup.workflow.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cool.drinkup.drinkup.workflow.internal.model.Wine;

@Repository
public interface WineRepository extends JpaRepository<Wine, Long> {
    Page<Wine> findByTagMainBaseSpirit(String tagMainBaseSpirit, Pageable pageable);
    
    Page<Wine> findAll(Pageable pageable);
}
