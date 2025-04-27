package cool.drinkup.drinkup.workflow.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import cool.drinkup.drinkup.workflow.internal.model.BarProcurement;

public interface BarProcurementRepository extends JpaRepository<BarProcurement, Long> {

    List<BarProcurement> findByBarId(Long barId);

    Optional<BarProcurement> findByIdAndBarId(Long procurementId, Long barId);
}