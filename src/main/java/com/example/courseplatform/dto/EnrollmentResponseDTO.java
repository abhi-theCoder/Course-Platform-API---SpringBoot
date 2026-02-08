package com.example.courseplatform.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EnrollmentResponseDTO {
    private Long enrollmentId;
    private String courseId;
    private String courseTitle;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime enrolledAt;
}
