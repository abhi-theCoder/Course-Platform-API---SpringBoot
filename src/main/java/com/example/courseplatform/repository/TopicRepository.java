package com.example.courseplatform.repository;

import com.example.courseplatform.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {

    @Query("SELECT t FROM Topic t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Topic> searchTopics(String query);
}
