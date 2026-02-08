package com.example.courseplatform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id; // Provided in seed data (e.g., "physics-101")

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Topic> topics = new ArrayList<>();
}
