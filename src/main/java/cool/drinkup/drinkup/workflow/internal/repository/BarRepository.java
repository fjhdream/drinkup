package cool.drinkup.drinkup.workflow.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import cool.drinkup.drinkup.workflow.internal.model.Bar;

public interface BarRepository extends JpaRepository<Bar, Long> {
    List<Bar> findByUserId(Long userId);
}
