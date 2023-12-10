package management.api.taskmanagementsystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "state")
public class TaskStateModel {
    @Id
    private Long stateId;
    @Column(unique = true, nullable = false)
    private String name;

    public Long getStateId() {
        return stateId;
    }

    public String getName() {
        return name;
    }
}
