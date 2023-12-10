package management.api.taskmanagementsystem.repo;

import management.api.taskmanagementsystem.models.PriorityModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PriorityRepository extends CrudRepository<PriorityModel, Long> {
    Optional<PriorityModel> findByName(String name);
}
