package io.github.jotagevm.daily_planner_api.dto;

import java.time.LocalTime;
import java.time.LocalDate;

import io.github.jotagevm.daily_planner_api.model.Recorrencia;
import io.github.jotagevm.daily_planner_api.model.TipoTarefa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TarefaRequest {
    @NotBlank
    private String nome;

    @NotBlank
    private String descricao;

    @NotNull
    private Long categoriaId;

    @NotNull
    private TipoTarefa tipo;

    @NotNull
    private Recorrencia recorrencia;

    private String diasSemana;

    private LocalTime horaInicio;

    @NotNull
    private LocalDate dataInicio;

    private Integer duracao;

    private Integer metaDiaria;
}
