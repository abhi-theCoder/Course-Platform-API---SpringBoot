package com.example.courseplatform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSummaryDTO {
    private String id;
    private String title;
    private String description;
    private int topicCount;
    private int subtopicCount;
}
