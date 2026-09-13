package io.github.jotagevm.daily_planner_api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OcorrenciaRequest {
    @NotNull
    private LocalDateTime dataHora;

    @NotNull
    private Long tarefaId;
}
