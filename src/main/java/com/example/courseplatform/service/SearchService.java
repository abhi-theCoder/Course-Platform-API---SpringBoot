package com.example.courseplatform.service;

import com.example.courseplatform.dto.SearchMatchDTO;
import com.example.courseplatform.dto.SearchResultDTO;
import com.example.courseplatform.model.Course;
import com.example.courseplatform.model.Subtopic;
import com.example.courseplatform.model.Topic;
import com.example.courseplatform.repository.CourseRepository;
import com.example.courseplatform.repository.SubtopicRepository;
import com.example.courseplatform.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final SubtopicRepository subtopicRepository;

    @Autowired
    public SearchService(CourseRepository courseRepository,
            TopicRepository topicRepository,
            SubtopicRepository subtopicRepository) {
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.subtopicRepository = subtopicRepository;
    }

    public List<SearchResultDTO> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, SearchResultDTO> resultsMap = new HashMap<>();

        // Search in Courses
        List<Course> courses = courseRepository.searchCourses(query);
        for (Course course : courses) {
            resultsMap
                    .computeIfAbsent(course.getId(),
                            k -> new SearchResultDTO(course.getId(), course.getTitle(), new ArrayList<>()))
                    .getMatches().add(new SearchMatchDTO("course", null, null, null, null));
        }

        // Search in Topics
        List<Topic> topics = topicRepository.searchTopics(query);
        for (Topic topic : topics) {
            Course course = topic.getCourse();
            resultsMap
                    .computeIfAbsent(course.getId(),
                            k -> new SearchResultDTO(course.getId(), course.getTitle(), new ArrayList<>()))
                    .getMatches().add(new SearchMatchDTO("topic", topic.getTitle(), null, null, null));
        }

        // Search in Subtopics (Title and Content)
        List<Subtopic> subtopics = subtopicRepository.searchSubtopics(query);
        for (Subtopic subtopic : subtopics) {
            Topic topic = subtopic.getTopic();
            Course course = topic.getCourse();

            String type = subtopic.getTitle().toLowerCase().contains(query.toLowerCase()) ? "subtopic" : "content";
            String snippet = getSnippet(subtopic.getContent(), query);

            resultsMap
                    .computeIfAbsent(course.getId(),
                            k -> new SearchResultDTO(course.getId(), course.getTitle(), new ArrayList<>()))
                    .getMatches()
                    .add(new SearchMatchDTO(type, topic.getTitle(), subtopic.getId(), subtopic.getTitle(), snippet));
        }

        return new ArrayList<>(resultsMap.values());
    }

    private String getSnippet(String content, String query) {
        if (content == null)
            return null;
        int index = content.toLowerCase().indexOf(query.toLowerCase());
        if (index == -1)
            return null;

        int start = Math.max(0, index - 30);
        int end = Math.min(content.length(), index + query.length() + 30);

        return (start > 0 ? "..." : "") + content.substring(start, end) + (end < content.length() ? "..." : "");
    }
}
