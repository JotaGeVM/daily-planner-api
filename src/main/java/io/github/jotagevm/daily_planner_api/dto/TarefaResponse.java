package io.github.jotagevm.daily_planner_api.dto;

import java.time.LocalTime;

import io.github.jotagevm.daily_planner_api.model.Recorrencia;
import io.github.jotagevm.daily_planner_api.model.TipoTarefa;

import lombok.Data;

@Data
public class TarefaResponse {
    private Long id;
    private String nome;
    private String descricao;
    private Long categoriaId;
    private String categoriaNome;
    private String categoriaCorHex;
    private TipoTarefa tipo;
    private Recorrencia recorrencia;
    private String diasSemana;
    private LocalTime horaInicio;
    private Integer duracao;
    private Integer metaDiaria;
}
