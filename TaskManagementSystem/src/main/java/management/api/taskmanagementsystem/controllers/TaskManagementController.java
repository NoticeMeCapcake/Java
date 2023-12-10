package management.api.taskmanagementsystem.controllers;

import management.api.taskmanagementsystem.contracts.OrderType;
import management.api.taskmanagementsystem.contracts.Priority;
import management.api.taskmanagementsystem.contracts.SortingType;
import management.api.taskmanagementsystem.contracts.StateType;
import management.api.taskmanagementsystem.models.TaskModel;
import management.api.taskmanagementsystem.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/task-management")
public class TaskManagementController {
    private final TaskManagementDataService taskManagementDataService;
    private final EmailValidator emailValidator;
    private final StringValidator stringValidator;
    private final RequestValidatorService requestValidatorService;
    private final TypeToStringResolver typeToStringResolver;

//    static final String[] requestFields = new[] {"tittle", "priority", "executor", "author"}

    public TaskManagementController(
            @Autowired TaskManagementDataService taskManagementDataService,
            @Autowired EmailValidator emailValidator,
            @Autowired StringValidator stringValidator,
            @Autowired RequestValidatorService requestValidatorService,
            @Autowired TypeToStringResolver typeToStringResolver) {
        this.taskManagementDataService = taskManagementDataService;
        this.emailValidator = emailValidator;
        this.stringValidator = stringValidator;
        this.requestValidatorService = requestValidatorService;
        this.typeToStringResolver = typeToStringResolver;
    }

    // JWT
    // Page ''/0/1/2/...
    // sorting SortingType (title, time, priority)
    // order asc/desc


    @GetMapping(path = "/tasks")
    public ResponseEntity<Map<String, Object>> getAllTasks(@RequestBody Map<String, String> body) {
        // Check if user is authorized

        // Check if fields are valid
        var requestFields = new String[]{"page", "sort", "order"};

        for (String field : requestFields) {
            if (!body.containsKey(field)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required field: " + field));
            }
        }

        int page = 0;
        try {
            var pageString = body.get("page");
            if (pageString != null && !pageString.isEmpty() && !pageString.isBlank()) {
                page = Integer.parseUnsignedInt(pageString);
            }
        }
        catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Page is invalid"));
        }

        var sort = SortingType.DATE;
        try {
            var sortString = body.get("sort");
            if (sortString != null && !sortString.isEmpty() && !sortString.isBlank()) {
                sort = SortingType.valueOf(sortString.toUpperCase());
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Sort is invalid"));
        }

        var order = OrderType.ASC;
        try {
            var orderString = body.get("order");
            if (stringValidator.isValid(orderString)) {
                order = OrderType.valueOf(orderString.toUpperCase());
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order is invalid"));
        }

        var sorting = (order == OrderType.ASC) ?
                Sort.by(typeToStringResolver.resolve(sort)).ascending() :
                Sort.by(typeToStringResolver.resolve(sort)).descending();

        var tasks = taskManagementDataService.getAllTasksPageable(PageRequest.of(0, 10, sorting));

        return ResponseEntity.ok().body(Map.of("tasks", tasks));
    }

    @GetMapping(path = "/tasks/executor={email}")
    public ResponseEntity<Map<String, Object>> getAllTasksAssignedToUser(@RequestBody Map<String, String> body, @PathVariable String email) {
        if (!emailValidator.isValid(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is invalid"));
        }
        var requestFields = new String[]{"page", "sort", "order"};

        for (String field : requestFields) {
            if (!body.containsKey(field)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required field: " + field));
            }
        }

        int page = 0;
        try {
            var pageString = body.get("page");
            if (pageString != null && !pageString.isEmpty() && !pageString.isBlank()) {
                page = Integer.parseUnsignedInt(pageString);
            }
        }
        catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Page is invalid"));
        }

        var sort = SortingType.DATE;
        try {
            var sortString = body.get("sort");
            if (sortString != null && !sortString.isEmpty() && !sortString.isBlank()) {
                sort = SortingType.valueOf(sortString.toUpperCase());
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Sort is invalid"));
        }

        var order = OrderType.ASC;
        try {
            var orderString = body.get("order");
            if (stringValidator.isValid(orderString)) {
                order = OrderType.valueOf(orderString.toUpperCase());
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order is invalid"));
        }

        Long executorId;
        try {
            executorId = taskManagementDataService.getUserIdByEmail(email);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        var sorting = (order == OrderType.ASC) ?
                Sort.by(typeToStringResolver.resolve(sort)).ascending() :
                Sort.by(typeToStringResolver.resolve(sort)).descending();

        var tasks = taskManagementDataService.getAllTasksByExecutorId(executorId, PageRequest.of(page, 10, sorting));

        return ResponseEntity.ok().body(Map.of("tasks", tasks));
    }

    @GetMapping(path = "/tasks/{id}")
    public ResponseEntity<Map<Object, Object>> getTaskById(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok().body(Map.of("task", taskManagementDataService.getTaskById(id)));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/tasks/author={email}")
    public ResponseEntity<Map<String, Object>> getAllTaskCreatedByUser(@RequestBody Map<String, String> body, @PathVariable String email) {
        if (!emailValidator.isValid(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is invalid"));
        }
        var requestFields = new String[]{"page", "sort", "order"};

        for (String field : requestFields) {
            if (!body.containsKey(field)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required field: " + field));
            }
        }

        int page = 0;
        try {
            var pageString = body.get("page");
            if (pageString != null && !pageString.isEmpty() && !pageString.isBlank()) {
                page = Integer.parseUnsignedInt(pageString);
            }
        }
        catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Page is invalid"));
        }

        var sort = SortingType.DATE;
        try {
            var sortString = body.get("sort");
            if (sortString != null && !sortString.isEmpty() && !sortString.isBlank()) {
                sort = SortingType.valueOf(sortString.toUpperCase());
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Sort is invalid"));
        }

        var order = OrderType.ASC;
        try {
            var orderString = body.get("order");
            if (stringValidator.isValid(orderString)) {
                order = OrderType.valueOf(orderString.toUpperCase());
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order is invalid"));
        }

        Long authorId;
        try {
            authorId = taskManagementDataService.getUserIdByEmail(email);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        var sorting = (order == OrderType.ASC) ?
                Sort.by(typeToStringResolver.resolve(sort)).ascending() :
                Sort.by(typeToStringResolver.resolve(sort)).descending();

        var tasks = taskManagementDataService.getAllTasksByAuthorId(authorId, PageRequest.of(page, 10, sorting));

        return ResponseEntity.ok().body(Map.of("tasks", tasks));
    }

    // JWT
    // title
    // executor email
    // creator email
    // priority int
    @PostMapping(path = "/tasks")
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Map<String, String> body) {
        var responseFromValidation = requestValidatorService.validateRequestBody(body,
                new String[]{"title", "state", "executor", "creator", "priority"});
        if (!responseFromValidation.getStatusCode().is2xxSuccessful()) {
            return responseFromValidation;
        }

        Priority priority;
        try {
            priority = Priority.valueOf(body.get("priority").toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Priority is invalid"));
        }

        StateType state;
        try {
            state = StateType.valueOf(body.get("priority").toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Priority is invalid"));
        }

        var title = body.get("title");
        var executor = body.get("executor");
        var creator = body.get("creator");
        if (!emailValidator.isValid(executor)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Executor is invalid"));
        }

        if (!emailValidator.isValid(creator)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Creator is invalid"));
        }

        Long executorId;
        try {
            executorId = taskManagementDataService.getUserIdByEmail(executor);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Executor is not found"));
        }

        Long Author;
        try {
            Author = taskManagementDataService.getUserIdByEmail(creator);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Creator is not found"));
        }

        var task = new TaskModel();
        task.setExecutorId(executorId);
        task.setAuthorId(Author);
        task.setTitle(title);
        task.setStateId((long) state.getValue());
        task.setPriorityId((long) priority.getValue());

        return ResponseEntity.ok().body(Map.of("task", task));
    }

    @PutMapping(path = "/tasks/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(@PathVariable Long id, @RequestBody Map<String, String> body) {
        var requestFields = new String[]{"title", "priority", "executor", "state"};

        for (String field : requestFields) {
            if (!body.containsKey(field)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required field: " + field));
            }

        }

        var newTitle = body.get("title");
        var newPriority = body.get("priority");
        var newExecutor = body.get("executor");
        var newState = body.get("state");
        TaskModel task;
        try {
            task = taskManagementDataService.getTaskById(id);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Task not found"));
        }

        if (stringValidator.isValid(newTitle)) {
            task.setTitle(newTitle);
        }
        if (stringValidator.isValid(newPriority)) {
            try {
                task.setPriorityId((long) Priority.valueOf(newPriority.toUpperCase()).getValue());
            }
            catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Priority is invalid"));
            }
        }
        if (stringValidator.isValid(newExecutor)) {
            Long executorId;
            if (!emailValidator.isValid(newExecutor)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Executor is invalid"));
            }
            try {
                executorId = taskManagementDataService.getUserIdByEmail(newExecutor);
            }
            catch (NoSuchElementException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Executor is not found"));
            }
            task.setExecutorId(executorId);
        }
        if (stringValidator.isValid(newState)) {
            try {
                task.setStateId((long) StateType.valueOf(newState.toUpperCase()).getValue());
            }
            catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "State is invalid"));
            }
        }
        try {
            taskManagementDataService.updateTask(task);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Task is invalid"));
        }

        return ResponseEntity.ok().body(Map.of("task", task));
    }

    @DeleteMapping(path = "/tasks/{id}")
    public ResponseEntity<Map<String, Object>>deleteTask(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (id == null || id <= 0 || !taskManagementDataService.deleteTaskById(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Id is invalid"));
        }

        return null;
    }
}
