package io.github.jotagevm.daily_planner_api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.jotagevm.daily_planner_api.dto.OcorrenciaRequest;
import io.github.jotagevm.daily_planner_api.dto.OcorrenciaResponse;
import io.github.jotagevm.daily_planner_api.exception.OcorrenciaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.exception.TarefaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.model.Ocorrencia;
import io.github.jotagevm.daily_planner_api.model.Tarefa;
import io.github.jotagevm.daily_planner_api.repository.OcorrenciaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;

@Service
public class OcorrenciaService {
    private final OcorrenciaRepository ocorrenciaRepository;
    private final TarefaRepository tarefaRepository;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository, TarefaRepository tarefaRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.tarefaRepository = tarefaRepository;
    }

    private void preencherCampos(Ocorrencia ocorrencia, OcorrenciaRequest dto) {
        Tarefa tarefa = tarefaRepository.findById(dto.getTarefaId())
                .orElseThrow(() -> new TarefaNaoEncontrada("Tarefa de ID " + dto.getTarefaId() + " não encontrada"));
        ocorrencia.setData(dto.getData());
        ocorrencia.setConcluida(dto.isConcluida());
        ocorrencia.setTarefa(tarefa);
    }

    private Ocorrencia converterEntidade(OcorrenciaRequest dto) {
        Ocorrencia ocorrencia = new Ocorrencia();
        preencherCampos(ocorrencia, dto);
        return ocorrencia;
    }

    private OcorrenciaResponse converterDto(Ocorrencia ocorrencia) {
        OcorrenciaResponse dto = new OcorrenciaResponse();
        dto.setId(ocorrencia.getId());
        dto.setData(ocorrencia.getData());
        dto.setConcluida(ocorrencia.isConcluida());
        dto.setTarefaNome(ocorrencia.getTarefa().getNome());

        return dto;
    }

    public OcorrenciaResponse criar(OcorrenciaRequest dto) {
        Ocorrencia ocorrencia = ocorrenciaRepository.save(converterEntidade(dto));
        return converterDto(ocorrencia);
    }

    public List<OcorrenciaResponse> listarPorData(LocalDate data) {
        return ocorrenciaRepository.findByData(data).stream()
                .map(this::converterDto)
                .collect(Collectors.toList());
    }

    public List<OcorrenciaResponse> listarPorTarefa(Long id) {
        return ocorrenciaRepository.findByTarefaId(id).stream()
                .map(this::converterDto)
                .collect(Collectors.toList());
    }

    public OcorrenciaResponse marcarComoConcluida(Long id, boolean concluida) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new OcorrenciaNaoEncontrada("Ocorrência de ID " + id + " não encontrada"));
        ocorrencia.setConcluida(concluida);

        return converterDto(ocorrenciaRepository.save(ocorrencia));
    }

    public void deletar(Long id) {
        if (!ocorrenciaRepository.existsById(id)) {
            throw new OcorrenciaNaoEncontrada("Ocorrência de ID " + id + " não encontrada");
        }
        ocorrenciaRepository.deleteById(id);
    }
}
