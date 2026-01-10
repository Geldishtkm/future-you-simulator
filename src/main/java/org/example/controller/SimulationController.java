package org.example.controller;

import org.example.*;
import org.example.dto.*;
import org.example.service.UserService;
import org.example.simulation.engine.FutureSimulationService;
import org.example.simulation.engine.SimulationInputBuilder;
import org.example.simulation.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for future simulation endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/simulation")
public class SimulationController {
    private final UserService userService;
    private final SimulationInputBuilder inputBuilder;
    private final FutureSimulationService simulationService;

    @Autowired
    public SimulationController(UserService userService,
                               SimulationInputBuilder inputBuilder,
                               FutureSimulationService simulationService) {
        this.userService = userService;
        this.inputBuilder = inputBuilder;
        this.simulationService = simulationService;
    }

    /**
     * Runs a future simulation for a user.
     *
     * @param userId the user ID
     * @param request the simulation request (years to simulate)
     * @return the simulation result
     */
    @PostMapping
    public ResponseEntity<SimulationResultDto> simulateFuture(
            @PathVariable Long userId,
            @RequestBody SimulationRequestDto request) {
        
        // Validate years to simulate
        int years = request.getYearsToSimulate();
        if (years < 1 || years > 5) {
            throw new IllegalArgumentException("Years to simulate must be between 1 and 5");
        }

        // Validate user exists
        userService.getUser(userId);

        // Get user data
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);

        // Build simulation input
        SimulationInput input = inputBuilder.build(userStats, habitService, goalService, years);

        // Run simulation
        SimulationResult result = simulationService.simulate(input);

        // Convert to DTO
        SimulationResultDto dto = toDto(result);

        return ResponseEntity.ok(dto);
    }

    /**
     * Converts SimulationResult to DTO.
     */
    private SimulationResultDto toDto(SimulationResult result) {
        SimulationResultDto dto = new SimulationResultDto();

        // Convert yearly projections
        List<YearlyProjectionDto> projectionDtos = result.getYearlyProjections().stream()
            .map(this::toProjectionDto)
            .collect(Collectors.toList());
        dto.setYearlyProjections(projectionDtos);

        dto.setAverageSkillGrowthIndex(result.getAverageSkillGrowthIndex());
        dto.setBurnoutRisk(result.getBurnoutRisk().name());
        
        // Convert income range
        IncomeRangeDto incomeDto = new IncomeRangeDto(
            result.getIncomeRange().getLowEstimate(),
            result.getIncomeRange().getExpectedEstimate(),
            result.getIncomeRange().getHighEstimate()
        );
        dto.setIncomeRange(incomeDto);

        dto.setEmigrationProbability(result.getEmigrationProbability());
        dto.setExplanation(result.getExplanation());

        return dto;
    }

    /**
     * Converts YearlyProjection to DTO.
     */
    private YearlyProjectionDto toProjectionDto(YearlyProjection projection) {
        return new YearlyProjectionDto(
            projection.getYear(),
            projection.getProjectedXp(),
            projection.getProjectedLevel(),
            projection.getSkillGrowthIndex(),
            projection.getXpGrowthRate()
        );
    }
}

