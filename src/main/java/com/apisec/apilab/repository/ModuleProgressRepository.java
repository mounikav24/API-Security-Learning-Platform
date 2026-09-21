package com.apisec.apilab.repository;

import com.apisec.apilab.entity.ModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, Long> {
    List<ModuleProgress> findByUserId(Long userId);
    Optional<ModuleProgress> findByUserIdAndModuleSlug(Long userId, String moduleSlug);
    boolean existsByUserIdAndModuleSlug(Long userId, String moduleSlug);
    long countByUserId(Long userId);
}
