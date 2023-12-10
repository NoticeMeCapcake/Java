package management.api.taskmanagementsystem.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "task_user")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = true)
    private String JWT;

    public Long getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public String getJWT() {
        return JWT;
    }
}
