package cool.drinkup.drinkup.workflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import cool.drinkup.drinkup.workflow.model.Bar;

public interface BarRepository extends JpaRepository<Bar, Long> {
    List<Bar> findByUserId(Long userId);
}
