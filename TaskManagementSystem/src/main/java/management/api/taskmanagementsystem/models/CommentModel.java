package management.api.taskmanagementsystem.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Comment")
public class CommentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private Long taskId;
    private Long userId;
    private String text;
    private LocalDateTime timeStamp;
}
