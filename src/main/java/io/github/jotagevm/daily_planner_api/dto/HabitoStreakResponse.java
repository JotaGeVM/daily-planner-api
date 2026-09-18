package io.github.jotagevm.daily_planner_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HabitoStreakResponse {
    private Integer streakAtual;
    private Integer melhorStreak;
}