package io.github.jotagevm.daily_planner_api.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalendarioDiaResponse {
    private LocalDate data;
    private List<TarefaResponse> tarefas;
}