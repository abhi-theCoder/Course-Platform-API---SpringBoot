package com.example.courseplatform.config;

import com.example.courseplatform.model.Course;
import com.example.courseplatform.model.Topic;
import com.example.courseplatform.model.Subtopic;
import com.example.courseplatform.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

@Component
public class DataLoader implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataLoader(CourseRepository courseRepository, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() == 0) {
            File seedDataFile = new File("seed_data/courses.json");
            if (seedDataFile.exists()) {
                CourseWrapper wrapper = objectMapper.readValue(seedDataFile, CourseWrapper.class);

                for (Course course : wrapper.getCourses()) {
                    // Set relationships
                    for (Topic topic : course.getTopics()) {
                        topic.setCourse(course);
                        for (Subtopic subtopic : topic.getSubtopics()) {
                            subtopic.setTopic(topic);
                        }
                    }
                    courseRepository.save(course);
                }

                System.out.println("Database seeded with courses from " + seedDataFile.getAbsolutePath());
            } else {
                System.out.println("Seed data file not found: " + seedDataFile.getAbsolutePath());
            }
        } else {
            System.out.println("Database already contains data. Skipping seed.");
        }
    }

    @Data
    static class CourseWrapper {
        private List<Course> courses;
    }
}
