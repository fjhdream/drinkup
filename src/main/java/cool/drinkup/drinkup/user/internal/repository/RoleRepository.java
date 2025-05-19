package cool.drinkup.drinkup.user.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cool.drinkup.drinkup.user.internal.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByName(String name);
} 