# Future You Simulator - REST API Endpoints

## Base URL
```
http://localhost:8080/api
```

## User Endpoints

### Create User
```
POST /users
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com"
}

Response: 201 Created
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "stats": {
    "totalXp": 0,
    "level": 1
  }
}
```

### Get User
```
GET /users/{id}

Response: 200 OK
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "stats": {
    "totalXp": 150,
    "level": 2
  }
}
```

## Habit Endpoints

### Create Habit
```
POST /users/{userId}/habits
Content-Type: application/json

{
  "name": "Morning Exercise",
  "difficulty": 3
}

Response: 201 Created
{
  "id": 1,
  "name": "Morning Exercise",
  "difficulty": 3
}
```

### Get User Habits
```
GET /users/{userId}/habits

Response: 200 OK
[
  {
    "id": 1,
    "name": "Morning Exercise",
    "difficulty": 3
  }
]
```

### Complete Habit
```
POST /users/{userId}/habits/{habitId}/complete?date=2025-01-15

Response: 200 OK
{
  "xpChange": 30,
  "newTotalXp": 130,
  "newLevel": 2,
  "reason": "Completed habit 'Morning Exercise' (difficulty 3)"
}
```

### Miss Habit
```
POST /users/{userId}/habits/{habitId}/miss?date=2025-01-15

Response: 200 OK
{
  "xpChange": -15,
  "newTotalXp": 115,
  "newLevel": 2,
  "reason": "Missed habit 'Morning Exercise' (difficulty 1)"
}
```

## Simulation Endpoints

### Run Future Simulation
```
POST /users/{userId}/simulation
Content-Type: application/json

{
  "yearsToSimulate": 3
}

Response: 200 OK
{
  "yearlyProjections": [
    {
      "year": 1,
      "projectedXp": 5000,
      "projectedLevel": 5,
      "skillGrowthIndex": 1.2,
      "xpGrowthRate": 0.15
    }
  ],
  "averageSkillGrowthIndex": 1.15,
  "burnoutRisk": "MEDIUM",
  "incomeRange": {
    "lowEstimate": 45000,
    "expectedEstimate": 55000,
    "highEstimate": 65000
  },
  "emigrationProbability": 0.25,
  "explanation": "Based on your current habits and goals..."
}
```

## Strategy Endpoints

### Get Recommendations
```
GET /users/{userId}/strategy/recommendations?years=3

Response: 200 OK
[
  {
    "type": "REDUCE_BURNOUT_RISK",
    "description": "Reduce daily effort to avoid burnout",
    "reason": "Your burnout risk is HIGH due to excessive daily effort",
    "expectedBenefit": "Lower burnout risk, improved long-term consistency",
    "riskNote": "May temporarily reduce XP gains",
    "impact": "HIGH",
    "priorityScore": 9.5
  }
]
```

### Generate Improved Scenarios
```
POST /users/{userId}/strategy/scenarios?years=3

Response: 200 OK
[
  {
    "scenarioName": "Improved Consistency Scenario",
    "rationale": "Applied recommendations to improve consistency",
    "parameterChanges": {
      "dailyEffort": "7.0 -> 6.5",
      "missedActionsPerMonth": "5 -> 3"
    },
    "xpImprovement": 1250.0,
    "skillGrowthImprovement": 0.15,
    "burnoutRiskChange": "HIGH -> MEDIUM",
    "incomeProjectionDelta": 5000,
    "emigrationProbabilityChange": -0.05,
    "improvementDescription": "This scenario shows...",
    "baseResult": { ... },
    "improvedResult": { ... }
  }
]
```

## Goal Endpoints

### Create Goal
```
POST /users/{userId}/goals
Content-Type: application/json

{
  "title": "Get a Backend Internship",
  "description": "Land an internship at a tech company",
  "startDate": "2025-01-01",
  "targetDate": "2025-06-30",
  "importance": 5,
  "totalProgressPoints": 100
}

Response: 201 Created
{
  "id": 1,
  "title": "Get a Backend Internship",
  "description": "Land an internship at a tech company",
  "startDate": "2025-01-01",
  "targetDate": "2025-06-30",
  "importance": 5,
  "totalProgressPoints": 100,
  "progressPercentage": 0.0
}
```

### Get User Goals
```
GET /users/{userId}/goals

Response: 200 OK
[
  {
    "id": 1,
    "title": "Get a Backend Internship",
    "progressPercentage": 38.0,
    ...
  }
]
```

### Add Goal Note
```
POST /users/{userId}/goals/{goalId}/notes?date=2025-01-15
Content-Type: application/json

{
  "textNote": "Applied to 3 companies today",
  "requestedXp": 8
}

Response: 200 OK
{
  "xpChange": 8,
  "newTotalXp": 158,
  "newLevel": 2,
  "reason": "Goal 'Get a Backend Internship' note: 8 XP assigned"
}
```

## Analytics Endpoints

### Get Dashboard
```
GET /api/users/{userId}/analytics/dashboard

Response: 200 OK
{
  "totalXp": 5000,
  "level": 5,
  "totalHabits": 5,
  "activeHabits": 5,
  "totalGoals": 3,
  "activeGoals": 3,
  "consistencyScore": 0.85,
  "currentStreaks": 15,
  "longestStreak": 30,
  "activeDaysLastWeek": 6,
  "trendDirection": "IMPROVING",
  "trendStrength": "LOW",
  "burnoutRisk": "LOW",
  "burnoutMessage": "No burnout indicators detected",
  "topStreaks": [
    {
      "habitName": "Morning Exercise",
      "currentStreak": 10,
      "longestStreak": 15,
      "lastActivityDate": "2025-01-15"
    }
  ],
  "recentActivity": [
    {
      "date": "2025-01-15",
      "xpChange": 50,
      "source": "HABIT"
    }
  ]
}
```

### Get Detailed Analytics
```
GET /api/users/{userId}/analytics/detailed

Response: 200 OK
{
  "consistencyScore": 0.85,
  "habitStreaks": [
    {
      "habitName": "Morning Exercise",
      "currentStreak": 10,
      "longestStreak": 15,
      "lastActivityDate": "2025-01-15"
    }
  ],
  "trends": [
    {
      "startDate": "2025-01-01",
      "endDate": "2025-01-15",
      "direction": "IMPROVING",
      "strength": "LOW",
      "description": "XP trend over last 14 days: IMPROVING"
    }
  ],
  "burnoutWarning": {
    "severity": "LOW",
    "message": "No burnout indicators detected",
    "indicators": []
  },
  "xpHistory": [
    {
      "date": "2025-01-15",
      "xpChange": 50,
      "source": "HABIT"
    }
  ]
}
```

### Get Health Score
```
GET /api/users/{userId}/analytics/health-score

Response: 200 OK
{
  "overallScore": 78.5,
  "consistencyScore": 85.0,
  "streakScore": 70.0,
  "trendScore": 90.0,
  "burnoutScore": 25.0,
  "healthLevel": "GOOD",
  "strengths": [
    "Strong consistency in maintaining habits and goals",
    "Positive momentum in activity",
    "Low burnout risk - sustainable pace"
  ],
  "areasForImprovement": [
    "Build longer streaks - target 30+ days"
  ],
  "recommendation": "You're on a good track! Focus on consistency to reach the next level."
}
```

## Planning Endpoints

### Get Five-Year Plan
```
GET /api/users/{userId}/plan/five-year?years=5

Response: 200 OK
{
  "currentStatus": {
    "totalXp": 5000,
    "level": 5,
    "totalHabits": 5,
    "activeHabits": 5,
    "totalGoals": 3,
    "activeGoals": 3,
    "consistencyScore": 0.85,
    "currentStreaks": 15,
    "longestStreak": 30,
    "activeDaysLastWeek": 6,
    "trendDirection": "IMPROVING",
    "trendStrength": "LOW",
    "burnoutRisk": "LOW",
    "burnoutMessage": "No burnout indicators detected",
    "topStreaks": [...],
    "recentActivity": [...]
  },
  "healthScore": {
    "overallScore": 78.5,
    "consistencyScore": 85.0,
    "streakScore": 70.0,
    "trendScore": 90.0,
    "burnoutScore": 25.0,
    "healthLevel": "GOOD",
    "strengths": [...],
    "areasForImprovement": [...],
    "recommendation": "..."
  },
  "baseSimulation": {
    "yearlyProjections": [...],
    "averageSkillGrowthIndex": 1.15,
    "burnoutRisk": "MEDIUM",
    "incomeRange": {...},
    "emigrationProbability": 0.25,
    "explanation": "..."
  },
  "topRecommendations": [
    {
      "type": "REDUCE_BURNOUT_RISK",
      "description": "Reduce daily effort intensity",
      "priorityScore": 9.5,
      ...
    }
  ],
  "bestScenarios": [
    {
      "scenarioName": "Improved Consistency Scenario",
      "xpImprovement": 1250.0,
      "skillGrowthImprovement": 0.15,
      ...
    }
  ],
  "summary": "Current Status: Level 5 with 5000 XP. Health Score: 78.5 (GOOD)...",
  "actionPlan": "Action Plan:\n\nPriority Actions:\n1. Reduce daily effort intensity\n...",
  "keyMilestones": [
    "Year 1: Reach Level 6 with 7500 XP (Skill Growth: 115.0%)",
    "Year 2: Reach Level 8 with 12000 XP (Skill Growth: 110.0%)",
    ...
  ],
  "projectedImprovement": 1250.0
}
```

## Error Responses

All errors follow this format:
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users",
  "validationErrors": [
    "username: Username is required",
    "email: Email must be valid"
  ]
}
```

## Status Codes

- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Validation error or invalid input
- `404 Not Found` - Resource not found
- `409 Conflict` - Business rule violation (e.g., duplicate habit check)
- `500 Internal Server Error` - Server error

