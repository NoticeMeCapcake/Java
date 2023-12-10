package management.api.taskmanagementsystem.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "priority")
public class PriorityModel {
    @Id
    @Column(name = "priority_id", unique = true, nullable = false)
    private Long priorityId;
    @Column(unique = true, nullable = false)
    private String name;

    public Long getPriorityId() {
        return priorityId;
    }

    public String getName() {
        return name;
    }
}
