package com.example.courseplatform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponseDTO {
    private Long enrollmentId;
    private String courseId;
    private String courseTitle;
    private int totalSubtopics;
    private int completedSubtopics;
    private double completionPercentage;
    private List<CompletedSubtopicDTO> completedItems;
}
