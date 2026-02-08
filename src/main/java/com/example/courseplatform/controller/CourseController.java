package com.example.courseplatform.controller;

import com.example.courseplatform.dto.CourseSummaryDTO;
import com.example.courseplatform.model.Course;
import com.example.courseplatform.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<CourseSummaryDTO>>> getAllCourses() {
        List<CourseSummaryDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(Map.of("courses", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
