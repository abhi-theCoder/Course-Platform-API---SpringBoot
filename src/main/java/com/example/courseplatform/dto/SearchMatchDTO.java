package com.example.courseplatform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchMatchDTO {
    private String type; // "course", "topic", "subtopic", "content"
    private String topicTitle;
    private String subtopicId;
    private String subtopicTitle;
    private String snippet;
}
