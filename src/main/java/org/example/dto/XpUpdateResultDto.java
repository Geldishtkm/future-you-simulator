package org.example.dto;

/**
 * DTO representing the result of an XP update operation.
 * 
 * Example response:
 * {
 *   "xpChange": 30,
 *   "newTotalXp": 130,
 *   "newLevel": 2,
 *   "reason": "Completed habit 'Morning Exercise' (difficulty 3)"
 * }
 */
public class XpUpdateResultDto {
    private Integer xpChange;
    private Integer newTotalXp;
    private Integer newLevel;
    private String reason;

    public XpUpdateResultDto() {
    }

    public XpUpdateResultDto(Integer xpChange, Integer newTotalXp, Integer newLevel, String reason) {
        this.xpChange = xpChange;
        this.newTotalXp = newTotalXp;
        this.newLevel = newLevel;
        this.reason = reason;
    }

    public Integer getXpChange() {
        return xpChange;
    }

    public void setXpChange(Integer xpChange) {
        this.xpChange = xpChange;
    }

    public Integer getNewTotalXp() {
        return newTotalXp;
    }

    public void setNewTotalXp(Integer newTotalXp) {
        this.newTotalXp = newTotalXp;
    }

    public Integer getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(Integer newLevel) {
        this.newLevel = newLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

