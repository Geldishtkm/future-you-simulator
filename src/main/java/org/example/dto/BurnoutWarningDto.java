package org.example.dto;

import java.util.List;

/**
 * DTO for burnout warning information.
 */
public class BurnoutWarningDto {
    private String severity;
    private String message;
    private List<String> indicators;

    public BurnoutWarningDto() {
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<String> indicators) {
        this.indicators = indicators;
    }
}

