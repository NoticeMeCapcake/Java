package management.api.taskmanagementsystem.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Task")
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stateId;
    private Long authorId;
    private Long executorId;
    private Long priorityId;
    private String title;
    private LocalDateTime date;

    public void setExecutorId(Long id) {
        executorId = id;
    }
    public void setAuthorId(Long id) {
        authorId = id;
    }
    public void setStateId(Long id) {
        stateId = id;
    }
    public void setPriorityId(Long id) {
        priorityId = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @JoinColumn(name = "state_id", referencedColumnName = "state_id", foreignKey = @ForeignKey(name = "fk_task_state"))
    @ManyToOne(fetch = FetchType.EAGER)
    @Transient
    private TaskStateModel state;
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "fk_task_author"))
    @ManyToOne(fetch = FetchType.EAGER)
    @Transient
    private UserModel author;
    @JoinColumn(name = "executor_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "fk_task_executor"))
    @ManyToOne(fetch = FetchType.EAGER)
    @Transient
    private UserModel executor;
    @JoinColumn(name = "priority_id", referencedColumnName = "priority_id" ,foreignKey = @ForeignKey(name = "fk_task_priority"))
    @ManyToOne(fetch = FetchType.EAGER)
    @Transient
    private PriorityModel priority;

    public TaskModel() {

    }
}
