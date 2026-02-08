package com.example.courseplatform.service;

import com.example.courseplatform.dto.EnrollmentResponseDTO;
import com.example.courseplatform.model.Course;
import com.example.courseplatform.model.Enrollment;
import com.example.courseplatform.model.User;
import com.example.courseplatform.repository.CourseRepository;
import com.example.courseplatform.repository.EnrollmentRepository;
import com.example.courseplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EnrollmentResponseDTO enrollUser(String email, String courseId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("You are already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return new EnrollmentResponseDTO(
                savedEnrollment.getId(),
                course.getId(),
                course.getTitle(),
                savedEnrollment.getEnrolledAt());
    }
}
