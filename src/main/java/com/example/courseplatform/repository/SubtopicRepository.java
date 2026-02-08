package com.example.courseplatform.repository;

import com.example.courseplatform.model.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubtopicRepository extends JpaRepository<Subtopic, String> {

    @Query("SELECT s FROM Subtopic s WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Subtopic> searchSubtopics(String query);
}
