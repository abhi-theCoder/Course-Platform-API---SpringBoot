package com.example.courseplatform.repository;

import com.example.courseplatform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Course> searchCourses(String query);
}
