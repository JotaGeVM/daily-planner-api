package io.github.jotagevm.daily_planner_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.jotagevm.daily_planner_api.dto.TarefaRequest;
import io.github.jotagevm.daily_planner_api.dto.TarefaResponse;
import io.github.jotagevm.daily_planner_api.service.TarefaService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {
    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping()
    public List<TarefaResponse> listarTodos() {
        return tarefaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponse> buscarPorId(@PathVariable Long id) {
        TarefaResponse tarefa = tarefaService.buscarPorId(id);
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    public ResponseEntity<TarefaResponse> criar(@RequestBody @Valid TarefaRequest tarefaDto) {
        TarefaResponse tarefa = tarefaService.salvar(tarefaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponse> atualizar(@PathVariable Long id,
            @RequestBody @Valid TarefaRequest tarefaDto) {
        TarefaResponse tarefa = tarefaService.atualizar(id, tarefaDto);
        return ResponseEntity.ok(tarefa);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tarefaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
