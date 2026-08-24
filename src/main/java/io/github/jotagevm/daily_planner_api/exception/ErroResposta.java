package io.github.jotagevm.daily_planner_api.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErroResposta {
    private int status;
    private String mensagem;
    private LocalDateTime timestamp;
}
