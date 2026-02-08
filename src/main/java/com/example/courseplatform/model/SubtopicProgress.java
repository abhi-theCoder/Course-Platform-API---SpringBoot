package com.example.courseplatform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "subtopic_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "enrollment_id", "subtopic_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtopicProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtopic_id", nullable = false)
    private Subtopic subtopic;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        completedAt = LocalDateTime.now();
    }
}
