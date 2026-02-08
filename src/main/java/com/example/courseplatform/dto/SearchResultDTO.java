package com.example.courseplatform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    private String courseId;
    private String courseTitle;
    private List<SearchMatchDTO> matches;
}
