package com.apisec.apilab.service;

import com.apisec.apilab.catalog.CurriculumCatalog;
import com.apisec.apilab.catalog.DashboardSnapshot;
import com.apisec.apilab.catalog.LearningModule;
import com.apisec.apilab.catalog.ModuleProgressView;
import com.apisec.apilab.catalog.QuizQuestion;
import com.apisec.apilab.entity.AssessmentAttempt;
import com.apisec.apilab.entity.ModuleProgress;
import com.apisec.apilab.entity.User;
import com.apisec.apilab.exception.ResourceNotFoundException;
import com.apisec.apilab.repository.AssessmentAttemptRepository;
import com.apisec.apilab.repository.ModuleProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LearningService {

    private final CurriculumCatalog catalog;
    private final ModuleProgressRepository moduleProgressRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;

    public LearningService(
            CurriculumCatalog catalog,
            ModuleProgressRepository moduleProgressRepository,
            AssessmentAttemptRepository assessmentAttemptRepository) {
        this.catalog = catalog;
        this.moduleProgressRepository = moduleProgressRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
    }

    public CurriculumCatalog catalog() {
        return catalog;
    }

    public LearningModule requireModule(String slug) {
        return catalog.module(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
    }

    public com.apisec.apilab.catalog.LabPreview requireLab(String slug) {
        return catalog.lab(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Lab not found"));
    }

    @Transactional(readOnly = true)
    public DashboardSnapshot dashboard(User user) {
        Set<String> done = completedSlugs(user.getId());
        List<ModuleProgressView> views = catalog.modules().stream()
                .map(m -> toView(m, done.contains(m.slug())))
                .toList();
        LearningModule next = catalog.modules().stream()
                .filter(m -> !done.contains(m.slug()))
                .findFirst()
                .orElse(null);
        Integer lastPercent = latestAttempt(user.getId()).map(AssessmentAttempt::percent).orElse(null);
        return new DashboardSnapshot(
                catalog.modules().size(),
                done.size(),
                catalog.labs().size(),
                catalog.assessment().size(),
                lastPercent,
                next,
                views
        );
    }

    public List<ModuleProgressView> moduleViews(User user) {
        Set<String> done = completedSlugs(user.getId());
        return catalog.modules().stream()
                .map(m -> toView(m, done.contains(m.slug())))
                .toList();
    }

    public boolean isModuleCompleted(User user, String slug) {
        return moduleProgressRepository.existsByUserIdAndModuleSlug(user.getId(), slug);
    }

    @Transactional
    public void completeModule(User user, String slug) {
        requireModule(slug);
        if (moduleProgressRepository.existsByUserIdAndModuleSlug(user.getId(), slug)) {
            return;
        }
        ModuleProgress progress = new ModuleProgress();
        progress.setUserId(user.getId());
        progress.setModuleSlug(slug);
        moduleProgressRepository.save(progress);
    }

    @Transactional
    public AssessmentAttempt submitAssessment(User user, Map<String, String> answers) {
        List<QuizQuestion> questions = catalog.assessment();
        int score = 0;
        for (QuizQuestion question : questions) {
            String raw = answers.getOrDefault(question.id(), "");
            int chosen = parseChoice(raw);
            if (chosen == question.correctIndex()) {
                score++;
            }
        }
        AssessmentAttempt attempt = new AssessmentAttempt();
        attempt.setUserId(user.getId());
        attempt.setScore(score);
        attempt.setTotal(questions.size());
        return assessmentAttemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public AssessmentAttempt latestAttempt(User user) {
        return latestAttempt(user.getId()).orElse(null);
    }

    public List<String> gradedExplanations(Map<String, String> answers) {
        List<String> lines = new ArrayList<>();
        for (QuizQuestion question : catalog.assessment()) {
            int chosen = parseChoice(answers.getOrDefault(question.id(), ""));
            boolean ok = chosen == question.correctIndex();
            lines.add((ok ? "Correct. " : "Review. ") + question.explanation());
        }
        return lines;
    }

    private java.util.Optional<AssessmentAttempt> latestAttempt(Long userId) {
        return assessmentAttemptRepository.findTopByUserIdOrderByCompletedAtDesc(userId);
    }

    private Set<String> completedSlugs(Long userId) {
        return moduleProgressRepository.findByUserId(userId).stream()
                .map(ModuleProgress::getModuleSlug)
                .collect(Collectors.toSet());
    }

    private ModuleProgressView toView(LearningModule module, boolean completed) {
        return new ModuleProgressView(module, completed, completed ? "Completed" : "Not started");
    }

    private int parseChoice(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
