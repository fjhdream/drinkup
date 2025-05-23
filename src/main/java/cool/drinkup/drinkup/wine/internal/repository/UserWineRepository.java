package cool.drinkup.drinkup.wine.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cool.drinkup.drinkup.wine.internal.model.UserWine;

public interface UserWineRepository extends JpaRepository<UserWine, Long> {

    Page<UserWine> findByUserId(Long userId, Pageable pageable);
    
    @Query(value = "SELECT * FROM user_wine WHERE user_id = :userId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    UserWine findRandomUserWine(@Param("userId") Long userId);
} 