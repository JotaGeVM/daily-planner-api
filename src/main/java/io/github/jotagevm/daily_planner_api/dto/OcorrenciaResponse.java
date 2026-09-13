package io.github.jotagevm.daily_planner_api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OcorrenciaResponse {
    private Long id;
    private LocalDateTime dataHora;
    private Long tarefaId;
    private String tarefaNome;
}
