package com.apisec.apilab.controller;

import com.apisec.apilab.catalog.LabPreview;
import com.apisec.apilab.catalog.LearningModule;
import com.apisec.apilab.entity.AssessmentAttempt;
import com.apisec.apilab.entity.User;
import com.apisec.apilab.exception.ResourceNotFoundException;
import com.apisec.apilab.service.LearningService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class LearningController {

    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("snapshot", learningService.dashboard(user));
        return "dashboard";
    }

    @GetMapping("/modules")
    public String modules(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "modules");
        model.addAttribute("moduleViews", learningService.moduleViews(user));
        return "modules";
    }

    @GetMapping("/modules/{slug}")
    public String moduleDetail(
            @PathVariable String slug,
            @AuthenticationPrincipal User user,
            Model model) {
        LearningModule module = learningService.requireModule(slug);
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "modules");
        model.addAttribute("module", module);
        model.addAttribute("completed", learningService.isModuleCompleted(user, slug));
        learningService.catalog().labs().stream()
                .filter(lab -> slug.equals(lab.relatedModuleSlug()))
                .findFirst()
                .ifPresent(lab -> model.addAttribute("relatedLab", lab));
        return "module-detail";
    }

    @PostMapping("/modules/{slug}/complete")
    public String completeModule(
            @PathVariable String slug,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        learningService.completeModule(user, slug);
        redirectAttributes.addFlashAttribute("formSuccess", "Module marked complete.");
        return "redirect:/modules/" + slug;
    }

    @GetMapping("/labs")
    public String labs(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "labs");
        model.addAttribute("labs", learningService.catalog().labs());
        return "labs";
    }

    @GetMapping("/labs/{slug}")
    public String labDetail(
            @PathVariable String slug,
            @AuthenticationPrincipal User user,
            Model model) {
        LabPreview lab = learningService.requireLab(slug);
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "labs");
        model.addAttribute("lab", lab);
        learningService.catalog().module(lab.relatedModuleSlug())
                .ifPresent(module -> model.addAttribute("relatedModule", module));
        return "lab-detail";
    }

    @GetMapping("/explorer")
    public String explorer(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "explorer");
        model.addAttribute("endpoints", learningService.catalog().explorer());
        return "explorer";
    }

    @GetMapping("/assessment")
    public String assessment(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "assessment");
        model.addAttribute("questions", learningService.catalog().assessment());
        model.addAttribute("latest", learningService.latestAttempt(user));
        return "assessment";
    }

    @PostMapping("/assessment")
    public String submitAssessment(
            @AuthenticationPrincipal User user,
            @RequestParam Map<String, String> params,
            Model model) {
        AssessmentAttempt attempt = learningService.submitAssessment(user, params);
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "assessment");
        model.addAttribute("questions", learningService.catalog().assessment());
        model.addAttribute("latest", attempt);
        model.addAttribute("explanations", learningService.gradedExplanations(params));
        model.addAttribute("submitted", true);
        return "assessment";
    }

    @GetMapping("/progress")
    public String progress(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "progress");
        model.addAttribute("snapshot", learningService.dashboard(user));
        model.addAttribute("latest", learningService.latestAttempt(user));
        return "progress";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String notFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("formError", "That item is not in the current catalog.");
        return "redirect:/modules";
    }
}
