package org.example.dto;

/**
 * Request DTO for running a future simulation.
 */
public class SimulationRequestDto {
    private int yearsToSimulate; // 1-5 years

    public SimulationRequestDto() {
    }

    public SimulationRequestDto(int yearsToSimulate) {
        this.yearsToSimulate = yearsToSimulate;
    }

    public int getYearsToSimulate() {
        return yearsToSimulate;
    }

    public void setYearsToSimulate(int yearsToSimulate) {
        this.yearsToSimulate = yearsToSimulate;
    }
}

