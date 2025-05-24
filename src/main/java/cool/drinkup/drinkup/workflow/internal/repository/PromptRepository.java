package cool.drinkup.drinkup.workflow.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cool.drinkup.drinkup.workflow.internal.model.PromptContent;

public interface PromptRepository extends JpaRepository<PromptContent, Long> {

    PromptContent findByType(String type);

}
