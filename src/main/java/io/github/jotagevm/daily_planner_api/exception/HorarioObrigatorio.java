package io.github.jotagevm.daily_planner_api.exception;

public class HorarioObrigatorio extends RuntimeException {
    public HorarioObrigatorio(String mensagem) {
        super(mensagem);
    }
}
