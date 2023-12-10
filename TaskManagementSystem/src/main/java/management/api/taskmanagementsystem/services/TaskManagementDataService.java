package management.api.taskmanagementsystem.services;

import jakarta.transaction.Transactional;
import management.api.taskmanagementsystem.models.PriorityModel;
import management.api.taskmanagementsystem.models.TaskModel;
import management.api.taskmanagementsystem.repo.PriorityRepository;
import management.api.taskmanagementsystem.repo.TaskRepository;
import management.api.taskmanagementsystem.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TaskManagementDataService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PriorityRepository priorityRepository;
    public TaskManagementDataService(
            @Autowired TaskRepository taskRepository,
            @Autowired UserRepository userRepository,
            @Autowired PriorityRepository priorityRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.priorityRepository = priorityRepository;
    }

    @Transactional
    public boolean deleteTaskById(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            return false;
        }
        taskRepository.deleteById(taskId);
        return true;
    }

    @Transactional
    public Long getPriorityIdByName(String name) throws NoSuchElementException {
        var priority = getPriorityByNameInternal(name);
        return priority.getPriorityId();
    }

    @Transactional
    public Long getUserIdByEmail(String email) throws NoSuchElementException {
        return getUserIdInternal(email);
    }

    @Transactional
    public List<TaskModel> getAllTasksByAuthorId(Long authorId, Pageable page) {
        return taskRepository.findAllByAuthorId(authorId, page);
    }

    @Transactional
    public List<TaskModel> getAllTasksByExecutorId(Long executorId, Pageable page) {
        return taskRepository.findAllByExecutorId(executorId, page);
    }

    @Transactional
    public boolean addTask(TaskModel task) {
        try {
            addTaskInternal(task);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public List<TaskModel> getAllTasksPageable(Pageable page) {
        return getAllTasksInternal(page);
    }

    private List<TaskModel> getAllTasksInternal(Pageable page) {
        return taskRepository.findAll(page).getContent();
    }

    private void addTaskInternal(TaskModel task) {
        taskRepository.save(task);
    }

    private Long getUserIdInternal(String email) {
        return userRepository.findByEmail(email).orElseThrow().getUserId();
    }

    private PriorityModel getPriorityByNameInternal(String name) throws NoSuchElementException {
        return priorityRepository.findByName(name).orElseThrow();
    }

    public TaskModel getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public void updateTask(TaskModel task) {
        taskRepository.save(task);
    }
}
