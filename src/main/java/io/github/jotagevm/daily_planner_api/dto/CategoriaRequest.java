package io.github.jotagevm.daily_planner_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequest {
    @NotBlank
    private String nome;

    private String descricao;

    @NotBlank
    private String corHex;
}
