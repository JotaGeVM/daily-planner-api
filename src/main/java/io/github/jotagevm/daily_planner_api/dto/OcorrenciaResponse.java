package io.github.jotagevm.daily_planner_api.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OcorrenciaResponse {
    private Long id;
    private LocalDate data;
    private boolean concluida;
    private String tarefaNome;
}
