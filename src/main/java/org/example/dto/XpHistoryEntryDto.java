package org.example.dto;

import java.time.LocalDate;

/**
 * DTO for XP history entry.
 */
public class XpHistoryEntryDto {
    private LocalDate date;
    private int xpChange;
    private String source;

    public XpHistoryEntryDto() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getXpChange() {
        return xpChange;
    }

    public void setXpChange(int xpChange) {
        this.xpChange = xpChange;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

