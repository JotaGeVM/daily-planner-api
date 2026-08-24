package io.github.jotagevm.daily_planner_api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OcorrenciaRequest {
    @NotNull
    private LocalDate data;

    private boolean concluida;

    @NotNull
    private Long tarefaId;
}
