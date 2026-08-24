package io.github.jotagevm.daily_planner_api.exception;

public class OcorrenciaNaoEncontrada extends RuntimeException {
    public OcorrenciaNaoEncontrada(String mensagem) {
        super(mensagem);
    }
}
