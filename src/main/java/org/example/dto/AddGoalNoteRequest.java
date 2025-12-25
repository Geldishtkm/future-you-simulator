package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for adding a daily goal note.
 * 
 * Example request:
 * {
 *   "textNote": "Applied to 3 companies today",
 *   "requestedXp": 8
 * }
 */
public class AddGoalNoteRequest {
    private String textNote;

    @NotNull(message = "Requested XP is required")
    @Min(value = 0, message = "Requested XP cannot be negative")
    private Integer requestedXp;

    public AddGoalNoteRequest() {
    }

    public AddGoalNoteRequest(String textNote, Integer requestedXp) {
        this.textNote = textNote;
        this.requestedXp = requestedXp;
    }

    public String getTextNote() {
        return textNote;
    }

    public void setTextNote(String textNote) {
        this.textNote = textNote;
    }

    public Integer getRequestedXp() {
        return requestedXp;
    }

    public void setRequestedXp(Integer requestedXp) {
        this.requestedXp = requestedXp;
    }
}

