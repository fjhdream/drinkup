package cool.drinkup.drinkup.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cool.drinkup.drinkup.user.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByName(String name);
} 