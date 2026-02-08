package com.example.courseplatform.repository;

import com.example.courseplatform.model.SubtopicProgress;
import com.example.courseplatform.model.Enrollment;
import com.example.courseplatform.model.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubtopicProgressRepository extends JpaRepository<SubtopicProgress, Long> {
    List<SubtopicProgress> findByEnrollment(Enrollment enrollment);

    Optional<SubtopicProgress> findByEnrollmentAndSubtopic(Enrollment enrollment, Subtopic subtopic);

    long countByEnrollment(Enrollment enrollment);
}
