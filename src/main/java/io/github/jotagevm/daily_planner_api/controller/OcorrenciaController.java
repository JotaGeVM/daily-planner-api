package io.github.jotagevm.daily_planner_api.controller;

import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import io.github.jotagevm.daily_planner_api.dto.OcorrenciaRequest;
import io.github.jotagevm.daily_planner_api.dto.OcorrenciaResponse;
import io.github.jotagevm.daily_planner_api.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {
    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @GetMapping
    public List<OcorrenciaResponse> listarTodas() {
        return ocorrenciaService.listarTodas();
    }

    @GetMapping("/tarefa/{id}")
    public ResponseEntity<List<OcorrenciaResponse>> listarPorTarefa(@PathVariable Long id) {
        List<OcorrenciaResponse> ocorrencia = ocorrenciaService.listarPorTarefa(id);
        return ResponseEntity.ok(ocorrencia);
    }

    @PostMapping
    public ResponseEntity<OcorrenciaResponse> criar(@RequestBody @Valid OcorrenciaRequest ocorrenciaDto) {
        OcorrenciaResponse ocorrenciaSalvo = ocorrenciaService.criar(ocorrenciaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ocorrenciaSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ocorrenciaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}