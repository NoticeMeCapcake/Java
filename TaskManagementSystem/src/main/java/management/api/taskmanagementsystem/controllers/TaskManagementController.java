package management.api.taskmanagementsystem.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/task-management")
public class TaskManagementController {
    @GetMapping
    public Map<Object, Object> getAllTasksAssignedToUser() {
        return null;
    }

    @GetMapping
    public Map<Object, Object> getTaskById() {
        return null;
    }

    @GetMapping
    public Map<Object, Object> getAllTaskCreatedByUser() {
        return null;
    }

    @PostMapping
    public Map<Object, Object> createTask() {
        return null;
    }

    @PutMapping
    public Map<Object, Object> updateTask() {
        return null;
    }



}
