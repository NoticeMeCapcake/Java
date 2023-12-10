package management.api.taskmanagementsystem.repo;

import management.api.taskmanagementsystem.models.TaskModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<TaskModel, Long>, CrudRepository<TaskModel, Long> {
    List<TaskModel> findAllByAuthorId(Long authorId, Pageable page);
    List<TaskModel> findAllByExecutorId(Long executorId, Pageable page);
}
