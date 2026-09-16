package io.github.jotagevm.daily_planner_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    private final UsuarioAtualProvider usuarioAtualProvider;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository, TarefaRepository tarefaRepository,
            UsuarioAtualProvider usuarioAtualProvider) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.tarefaRepository = tarefaRepository;
        this.usuarioAtualProvider = usuarioAtualProvider;
    }

    private void preencherCampos(Ocorrencia ocorrencia, OcorrenciaRequest dto, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findByIdAndUsuarioId(dto.getTarefaId(), usuarioId)
                .orElseThrow(() -> new TarefaNaoEncontrada("Tarefa de ID " + dto.getTarefaId() + " não encontrada"));
        ocorrencia.setDataHora(dto.getDataHora());
        ocorrencia.setTarefa(tarefa);
    }

    private OcorrenciaResponse converterDto(Ocorrencia ocorrencia) {
        OcorrenciaResponse dto = new OcorrenciaResponse();
        dto.setId(ocorrencia.getId());
        dto.setDataHora(ocorrencia.getDataHora());
        dto.setTarefaId(ocorrencia.getTarefa().getId());
        dto.setTarefaNome(ocorrencia.getTarefa().getNome());

        return dto;
    }

    public OcorrenciaResponse criar(OcorrenciaRequest dto) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        Ocorrencia ocorrencia = new Ocorrencia();
        preencherCampos(ocorrencia, dto, usuarioId);
        return converterDto(ocorrenciaRepository.save(ocorrencia));
    }

    public Page<OcorrenciaResponse> listarTodas(Pageable pageable) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        return ocorrenciaRepository.findByTarefa_UsuarioId(usuarioId, pageable).map(this::converterDto);
    }

    public List<OcorrenciaResponse> listarPorTarefa(Long id) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        return ocorrenciaRepository.findByTarefaIdAndTarefa_UsuarioId(id, usuarioId).stream()
                .map(this::converterDto)
                .collect(Collectors.toList());
    }

    public void deletar(Long id) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        ocorrenciaRepository.findByIdAndTarefa_UsuarioId(id, usuarioId)
                .orElseThrow(() -> new OcorrenciaNaoEncontrada("Ocorrência de ID " + id + " não encontrada"));
        ocorrenciaRepository.deleteById(id);
    }
}