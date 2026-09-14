package io.github.jotagevm.daily_planner_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.github.jotagevm.daily_planner_api.dto.OcorrenciaRequest;
import io.github.jotagevm.daily_planner_api.dto.OcorrenciaResponse;
import io.github.jotagevm.daily_planner_api.exception.OcorrenciaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.exception.TarefaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.model.Ocorrencia;
import io.github.jotagevm.daily_planner_api.model.Tarefa;
import io.github.jotagevm.daily_planner_api.repository.OcorrenciaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcorrenciaServiceTest {
    @Mock
    private OcorrenciaRepository ocorrenciaRepository;
    @Mock
    private TarefaRepository tarefaRepository;
    @InjectMocks
    private OcorrenciaService ocorrenciaService;

    private Tarefa tarefaFake() {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setNome("Academia");
        return tarefa;
    }

    @Test
    void criarOcorrenciaComSucesso() {
        OcorrenciaRequest request = new OcorrenciaRequest();
        request.setTarefaId(1L);
        request.setDataHora(LocalDateTime.of(2026, 9, 14, 14, 30));
        Tarefa tarefa = tarefaFake();
        Ocorrencia ocorrenciaSalva = new Ocorrencia();
        ocorrenciaSalva.setId(1L);
        ocorrenciaSalva.setTarefa(tarefa);
        ocorrenciaSalva.setDataHora(LocalDateTime.of(2026, 9, 14, 14, 30));
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(ocorrenciaRepository.save(any(Ocorrencia.class))).thenReturn(ocorrenciaSalva);
        OcorrenciaResponse resultado = ocorrenciaService.criar(request);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTarefaNome()).isEqualTo("Academia");
    }

    @Test
    void criarOcorrenciaComTarefaInexistente() {
        OcorrenciaRequest request = new OcorrenciaRequest();
        request.setTarefaId(99L);
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ocorrenciaService.criar(request)).isInstanceOf(TarefaNaoEncontrada.class);
        verify(ocorrenciaRepository, never()).save(any());
    }

    @Test
    void deletarIdOcorrenciaComSucesso() {
        when(ocorrenciaRepository.existsById(1L)).thenReturn(true);
        ocorrenciaService.deletar(1L);
        verify(ocorrenciaRepository).deleteById(1L);
    }

    @Test
    void deletarIdOcorrenciaInexistente() {
        when(ocorrenciaRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> ocorrenciaService.deletar(99L)).isInstanceOf(OcorrenciaNaoEncontrada.class);
        verify(ocorrenciaRepository, never()).deleteById(any());
    }

    @Test
    void listarTodasOcorrencias() {
        Ocorrencia ocorrencia1 = new Ocorrencia();
        ocorrencia1.setId(1L);
        ocorrencia1.setTarefa(tarefaFake());
        when(ocorrenciaRepository.findAll()).thenReturn(List.of(ocorrencia1));
        List<OcorrenciaResponse> resultado = ocorrenciaService.listarTodas();
        assertThat(resultado).hasSize(1);
    }

    @Test
    void listarOcorrenciasPorIdTarefa() {
        Ocorrencia ocorrencia1 = new Ocorrencia();
        ocorrencia1.setId(1L);
        ocorrencia1.setTarefa(tarefaFake());
        when(ocorrenciaRepository.findByTarefaId(1L)).thenReturn(List.of(ocorrencia1));
        List<OcorrenciaResponse> resultado = ocorrenciaService.listarPorTarefa(1L);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTarefaNome()).isEqualTo("Academia");
    }
}