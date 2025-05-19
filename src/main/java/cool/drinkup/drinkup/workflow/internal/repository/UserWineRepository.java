package cool.drinkup.drinkup.workflow.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cool.drinkup.drinkup.workflow.internal.model.UserWine;

public interface UserWineRepository extends JpaRepository<UserWine, Long> {

    Page<UserWine> findByUserId(Long userId, Pageable pageable);
}
