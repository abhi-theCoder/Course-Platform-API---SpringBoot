package com.example.courseplatform.controller;

import com.example.courseplatform.dto.EnrollmentResponseDTO;
import com.example.courseplatform.dto.ErrorResponseDTO;
import com.example.courseplatform.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/courses")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<?> enroll(@PathVariable String courseId, Authentication authentication) {
        try {
            String email = authentication.getName();
            EnrollmentResponseDTO enrollment = enrollmentService.enrollUser(email, courseId);
            return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("You are already enrolled in this course")) {
                return new ResponseEntity<>(
                        new ErrorResponseDTO("Already enrolled", e.getMessage(), LocalDateTime.now()),
                        HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(
                    new ErrorResponseDTO("Bad Request", e.getMessage(), LocalDateTime.now()),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
