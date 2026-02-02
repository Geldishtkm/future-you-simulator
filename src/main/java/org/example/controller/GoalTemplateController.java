package org.example.controller;

import org.example.GoalTemplate;
import org.example.dto.GoalTemplateDto;
import org.example.service.GoalTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for goal template endpoints.
 */
@RestController
@RequestMapping("/api/goal-templates")
public class GoalTemplateController {
    private final GoalTemplateService goalTemplateService;

    @Autowired
    public GoalTemplateController(GoalTemplateService goalTemplateService) {
        this.goalTemplateService = goalTemplateService;
    }

    /**
     * Get all goal templates.
     *
     * GET /api/goal-templates
     */
    @GetMapping
    public ResponseEntity<List<GoalTemplateDto>> getAllTemplates() {
        List<GoalTemplate> templates = goalTemplateService.getAllTemplates();
        List<GoalTemplateDto> dtos = templates.stream()
            .map(this::toGoalTemplateDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get templates by category.
     *
     * GET /api/goal-templates?category=Career
     */
    @GetMapping(params = "category")
    public ResponseEntity<List<GoalTemplateDto>> getTemplatesByCategory(@RequestParam String category) {
        List<GoalTemplate> templates = goalTemplateService.getTemplatesByCategory(category);
        List<GoalTemplateDto> dtos = templates.stream()
            .map(this::toGoalTemplateDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get a template by ID.
     *
     * GET /api/goal-templates/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GoalTemplateDto> getTemplateById(@PathVariable String id) {
        GoalTemplate template = goalTemplateService.getTemplateById(id);
        return ResponseEntity.ok(toGoalTemplateDto(template));
    }

    private GoalTemplateDto toGoalTemplateDto(GoalTemplate template) {
        GoalTemplateDto dto = new GoalTemplateDto();
        dto.setId(template.id());
        dto.setTitle(template.title());
        dto.setDescription(template.description());
        dto.setCategory(template.category());
        dto.setSuggestedDurationDays(template.suggestedDurationDays());
        dto.setSuggestedImportance(template.suggestedImportance());
        dto.setSuggestedSteps(List.of(template.suggestedSteps()));
        return dto;
    }
}

