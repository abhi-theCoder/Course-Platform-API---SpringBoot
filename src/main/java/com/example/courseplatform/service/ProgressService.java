package com.example.courseplatform.service;

import com.example.courseplatform.dto.CompletedSubtopicDTO;
import com.example.courseplatform.dto.ProgressResponseDTO;
import com.example.courseplatform.dto.SubtopicCompletionResponseDTO;
import com.example.courseplatform.model.*;
import com.example.courseplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgressService {

        private final SubtopicProgressRepository progressRepository;
        private final EnrollmentRepository enrollmentRepository;
        private final SubtopicRepository subtopicRepository;
        private final UserRepository userRepository;

        @Autowired
        public ProgressService(SubtopicProgressRepository progressRepository,
                        EnrollmentRepository enrollmentRepository,
                        SubtopicRepository subtopicRepository,
                        UserRepository userRepository) {
                this.progressRepository = progressRepository;
                this.enrollmentRepository = enrollmentRepository;
                this.subtopicRepository = subtopicRepository;
                this.userRepository = userRepository;
        }

        @Transactional
        public SubtopicCompletionResponseDTO markSubtopicComplete(String email, String subtopicId) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Subtopic subtopic = subtopicRepository.findById(subtopicId)
                                .orElseThrow(() -> new RuntimeException("Subtopic not found"));

                Course course = subtopic.getTopic().getCourse();

                Enrollment enrollment = enrollmentRepository.findByUserAndCourse(user, course)
                                .orElseThrow(() -> new RuntimeException(
                                                "Enrollment not found. You must be enrolled in the course."));

                Optional<SubtopicProgress> existingProgress = progressRepository.findByEnrollmentAndSubtopic(enrollment,
                                subtopic);
                if (existingProgress.isPresent()) {
                        return new SubtopicCompletionResponseDTO(
                                        subtopicId,
                                        true,
                                        existingProgress.get().getCompletedAt());
                }

                SubtopicProgress progress = new SubtopicProgress();
                progress.setEnrollment(enrollment);
                progress.setSubtopic(subtopic);

                SubtopicProgress savedProgress = progressRepository.save(progress);

                return new SubtopicCompletionResponseDTO(
                                subtopicId,
                                true,
                                savedProgress.getCompletedAt());
        }

        public ProgressResponseDTO getProgress(Long enrollmentId, String email) {
                Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

                // Security check: ensure the enrollment belongs to the requesting user
                if (!enrollment.getUser().getEmail().equals(email)) {
                        throw new RuntimeException("Access denied: You can only view your own progress.");
                }

                Course course = enrollment.getCourse();
                int totalSubtopics = course.getTopics().stream()
                                .mapToInt(topic -> topic.getSubtopics().size())
                                .sum();

                List<SubtopicProgress> progressList = progressRepository.findByEnrollment(enrollment);
                int completedSubtopics = progressList.size();

                double percentage = totalSubtopics > 0 ? ((double) completedSubtopics / totalSubtopics) * 100 : 0;

                List<CompletedSubtopicDTO> completedItems = progressList.stream()
                                .map(p -> new CompletedSubtopicDTO(
                                                p.getSubtopic().getId(),
                                                p.getSubtopic().getTitle(),
                                                p.getCompletedAt()))
                                .collect(Collectors.toList());

                return new ProgressResponseDTO(
                                enrollmentId,
                                course.getId(),
                                course.getTitle(),
                                totalSubtopics,
                                completedSubtopics,
                                Math.round(percentage * 100.0) / 100.0,
                                completedItems);
        }
}
