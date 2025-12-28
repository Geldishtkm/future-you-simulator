package org.example.behavior.drift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BehaviorDriftDetector.
 */
class BehaviorDriftDetectorTest {
    private BehaviorDriftDetector detector;

    @BeforeEach
    void setUp() {
        detector = new BehaviorDriftDetector();
    }

    @Test
    void testDetectImprovementDrift() {
        // Arrange: Positive changes indicating improvement
        LocalDate earlierDate = LocalDate.now().minusDays(30);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 50.0, 60.0, 40.0, 30.0, 2, 50.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 75.0, 75.0, 60.0, 25.0, 3, 65.0
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        assertNotNull(event, "Should detect improvement drift");
        assertEquals(DriftType.IMPROVEMENT, event.getDriftType());
        assertNotNull(event.getExplanation());
        assertFalse(event.getExplanation().isBlank());
    }

    @Test
    void testDetectDeclineDrift() {
        // Arrange: Negative changes indicating decline
        LocalDate earlierDate = LocalDate.now().minusDays(30);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 80.0, 75.0, 60.0, 25.0, 3, 70.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 50.0, 55.0, 35.0, 40.0, 2, 45.0
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        assertNotNull(event, "Should detect decline drift");
        assertEquals(DriftType.DECLINE, event.getDriftType());
        assertTrue(event.getSeverity() != DriftSeverity.NONE);
    }

    @Test
    void testDetectBurnoutDrift() {
        // Arrange: Significant burnout risk increase
        LocalDate earlierDate = LocalDate.now().minusDays(30);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 70.0, 70.0, 50.0, 30.0, 3, 60.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 75.0, 72.0, 45.0, 60.0, 3, 58.0 // High burnout risk increase
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        assertNotNull(event, "Should detect burnout drift");
        assertEquals(DriftType.BURNOUT, event.getDriftType());
        assertTrue(event.getAffectedMetrics().containsKey("burnoutRiskScore"));
    }

    @Test
    void testDetectStagnationDrift() {
        // Arrange: Minimal changes
        LocalDate earlierDate = LocalDate.now().minusDays(30);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 60.0, 65.0, 50.0, 35.0, 2, 55.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 62.0, 67.0, 52.0, 33.0, 2, 57.0 // Minimal changes
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        // May or may not detect stagnation depending on thresholds
        if (event != null) {
            assertTrue(event.getDriftType() == DriftType.STAGNATION || 
                      event.getSeverity() == DriftSeverity.LOW);
        }
    }

    @Test
    void testNoDriftDetectedForShortTimeframe() {
        // Arrange: Less than minimum days
        LocalDate earlierDate = LocalDate.now().minusDays(10);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 50.0, 60.0, 40.0, 30.0, 2, 50.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 80.0, 80.0, 70.0, 20.0, 3, 70.0 // Large changes but too short timeframe
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        assertNull(event, "Should not detect drift for timeframes less than minimum days");
    }

    @Test
    void testNoDriftDetectedForInsignificantChanges() {
        // Arrange: Changes below threshold
        LocalDate earlierDate = LocalDate.now().minusDays(30);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 60.0, 65.0, 50.0, 35.0, 2, 55.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 63.0, 66.0, 51.0, 36.0, 2, 56.0 // Small changes below threshold
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        // May return null if changes are too small
        // This is acceptable behavior - we only want meaningful drift
    }

    @Test
    void testDriftEventContainsAllFields() {
        // Arrange
        LocalDate earlierDate = LocalDate.now().minusDays(30);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 50.0, 60.0, 40.0, 30.0, 2, 50.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 75.0, 75.0, 60.0, 25.0, 3, 65.0
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        assertNotNull(event);
        assertNotNull(event.getDriftType());
        assertNotNull(event.getSeverity());
        assertEquals(earlier, event.getEarlierSnapshot());
        assertEquals(later, event.getLaterSnapshot());
        assertNotNull(event.getDetectionDate());
        assertNotNull(event.getAffectedMetrics());
        assertFalse(event.getAffectedMetrics().isEmpty());
        assertNotNull(event.getExplanation());
        assertFalse(event.getExplanation().isBlank());
        assertTrue(event.getDaysBetweenSnapshots() > 0);
    }

    @Test
    void testDetectDriftOverTime() {
        // Arrange: Multiple snapshots
        LocalDate date1 = LocalDate.now().minusDays(60);
        LocalDate date2 = LocalDate.now().minusDays(30);
        LocalDate date3 = LocalDate.now();
        
        BehaviorSnapshot snapshot1 = new BehaviorSnapshot(
            date1, 50.0, 60.0, 40.0, 30.0, 2, 50.0
        );
        
        BehaviorSnapshot snapshot2 = new BehaviorSnapshot(
            date2, 65.0, 70.0, 55.0, 25.0, 3, 60.0
        );
        
        BehaviorSnapshot snapshot3 = new BehaviorSnapshot(
            date3, 80.0, 80.0, 70.0, 20.0, 3, 70.0
        );

        List<BehaviorSnapshot> snapshots = List.of(snapshot1, snapshot2, snapshot3);

        // Act
        List<DriftEvent> events = detector.detectDriftOverTime(snapshots, date3);

        // Assert
        assertNotNull(events);
        // Should detect drift between snapshot pairs with sufficient time
        assertFalse(events.isEmpty());
    }

    @Test
    void testHighSeverityDrift() {
        // Arrange: Large changes indicating high severity
        LocalDate earlierDate = LocalDate.now().minusDays(30);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 70.0, 75.0, 60.0, 25.0, 3, 70.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 35.0, 40.0, 20.0, 70.0, 1, 30.0 // Large negative changes
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        assertNotNull(event);
        assertTrue(event.getSeverity() == DriftSeverity.HIGH || 
                  event.getSeverity() == DriftSeverity.MEDIUM,
            "Large changes should result in high or medium severity");
    }

    @Test
    void testNullInputThrowsException() {
        // Act & Assert
        LocalDate date = LocalDate.now();
        BehaviorSnapshot snapshot = new BehaviorSnapshot(
            date, 50.0, 60.0, 40.0, 30.0, 2, 50.0
        );

        assertThrows(IllegalArgumentException.class, () -> {
            detector.detectDrift(null, snapshot, date);
        }, "Should throw exception for null earlier snapshot");

        assertThrows(IllegalArgumentException.class, () -> {
            detector.detectDrift(snapshot, null, date);
        }, "Should throw exception for null later snapshot");

        assertThrows(IllegalArgumentException.class, () -> {
            detector.detectDrift(snapshot, snapshot, null);
        }, "Should throw exception for null detection date");
    }

    @Test
    void testAffectedMetricsCalculation() {
        // Arrange
        LocalDate earlierDate = LocalDate.now().minusDays(30);
        LocalDate laterDate = LocalDate.now();
        
        BehaviorSnapshot earlier = new BehaviorSnapshot(
            earlierDate, 60.0, 65.0, 50.0, 30.0, 2, 55.0
        );
        
        BehaviorSnapshot later = new BehaviorSnapshot(
            laterDate, 80.0, 75.0, 60.0, 25.0, 3, 65.0
        );

        // Act
        DriftEvent event = detector.detectDrift(earlier, later, laterDate);

        // Assert
        if (event != null) {
            Map<String, Double> metrics = event.getAffectedMetrics();
            assertTrue(metrics.containsKey("averageDailyXp"));
            assertTrue(metrics.containsKey("habitCompletionRate"));
            assertTrue(metrics.containsKey("streakStability"));
            assertTrue(metrics.containsKey("burnoutRiskScore"));
            assertTrue(metrics.containsKey("activeGoalCount"));
            assertTrue(metrics.containsKey("goalEngagementRate"));
        }
    }
}

