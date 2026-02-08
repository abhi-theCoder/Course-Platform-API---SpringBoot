package com.example.courseplatform.controller;

import com.example.courseplatform.dto.ErrorResponseDTO;
import com.example.courseplatform.dto.ProgressResponseDTO;
import com.example.courseplatform.dto.SubtopicCompletionResponseDTO;
import com.example.courseplatform.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class ProgressController {

    private final ProgressService progressService;

    @Autowired
    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/subtopics/{subtopicId}/complete")
    public ResponseEntity<?> markComplete(@PathVariable String subtopicId, Authentication authentication) {
        try {
            SubtopicCompletionResponseDTO result = progressService.markSubtopicComplete(authentication.getName(),
                    subtopicId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg.contains("not found")) {
                return new ResponseEntity<>(
                        new ErrorResponseDTO("Not Found", msg, LocalDateTime.now()),
                        HttpStatus.NOT_FOUND);
            }
            if (msg.contains("must be enrolled")) {
                return new ResponseEntity<>(
                        new ErrorResponseDTO("Forbidden", msg, LocalDateTime.now()),
                        HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(
                    new ErrorResponseDTO("Bad Request", msg, LocalDateTime.now()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<?> getProgress(@PathVariable Long enrollmentId, Authentication authentication) {
        try {
            ProgressResponseDTO progress = progressService.getProgress(enrollmentId, authentication.getName());
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Denied")) {
                return new ResponseEntity<>(
                        new ErrorResponseDTO("Forbidden", e.getMessage(), LocalDateTime.now()),
                        HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(
                    new ErrorResponseDTO("Not Found", e.getMessage(), LocalDateTime.now()),
                    HttpStatus.NOT_FOUND);
        }
    }
}
