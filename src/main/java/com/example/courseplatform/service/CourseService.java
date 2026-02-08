package com.example.courseplatform.service;

import com.example.courseplatform.dto.CourseSummaryDTO;
import com.example.courseplatform.model.Course;
import com.example.courseplatform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseSummaryDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    private CourseSummaryDTO convertToSummaryDTO(Course course) {
        int topicCount = course.getTopics().size();
        int subtopicCount = course.getTopics().stream()
                .mapToInt(topic -> topic.getSubtopics().size())
                .sum();

        return new CourseSummaryDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                topicCount,
                subtopicCount);
    }
}
