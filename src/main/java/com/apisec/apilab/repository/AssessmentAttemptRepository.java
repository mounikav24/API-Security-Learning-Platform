package com.apisec.apilab.repository;

import com.apisec.apilab.entity.AssessmentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, Long> {
    Optional<AssessmentAttempt> findTopByUserIdOrderByCompletedAtDesc(Long userId);
}
