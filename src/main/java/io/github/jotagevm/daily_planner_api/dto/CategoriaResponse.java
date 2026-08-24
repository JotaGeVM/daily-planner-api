package io.github.jotagevm.daily_planner_api.dto;

import lombok.Data;

@Data
public class CategoriaResponse {
    private Long id;
    private String nome;
    private String corHex;
}
