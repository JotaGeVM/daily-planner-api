package io.github.jotagevm.daily_planner_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.github.jotagevm.daily_planner_api.dto.TarefaRequest;
import io.github.jotagevm.daily_planner_api.dto.TarefaResponse;
import io.github.jotagevm.daily_planner_api.exception.CategoriaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.exception.DiasSemanaObrigatorio;
import io.github.jotagevm.daily_planner_api.exception.HorarioObrigatorio;
import io.github.jotagevm.daily_planner_api.exception.TarefaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.model.*;
import io.github.jotagevm.daily_planner_api.repository.CategoriaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {
    @Mock
    private TarefaRepository tarefaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @InjectMocks
    private TarefaService tarefaService;

    private Categoria categoriaFake() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Trabalho");
        return categoria;
    }

    private TarefaRequest requestValido() {
        TarefaRequest request = new TarefaRequest();
        request.setNome("Reunião");
        request.setDescricao("Reunião semanal do time");
        request.setCategoriaId(1L);
        request.setTipo(TipoTarefa.TAREFA);
        request.setRecorrencia(Recorrencia.NENHUMA);
        return request;
    }

    @Test
    void salvarTarefaComSucesso() {
        TarefaRequest request = requestValido();
        Categoria categoria = categoriaFake();
        Tarefa tarefaSalva = new Tarefa();
        tarefaSalva.setId(10L);
        tarefaSalva.setNome("Reunião");
        tarefaSalva.setDescricao("Reunião semanal do time");
        tarefaSalva.setCategoria(categoria);
        tarefaSalva.setTipo(TipoTarefa.TAREFA);
        tarefaSalva.setRecorrencia(Recorrencia.NENHUMA);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefaSalva);
        TarefaResponse resultado = tarefaService.salvar(request);
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getCategoriaNome()).isEqualTo("Trabalho");
    }

    @Test
    void idCategoriaNaoEncontrada() {
        TarefaRequest request = requestValido();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tarefaService.salvar(request)).isInstanceOf(CategoriaNaoEncontrada.class);
        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void recorrenciaSemanalSemDiasSemana() {
        TarefaRequest request = requestValido();
        request.setRecorrencia(Recorrencia.SEMANAL);
        request.setDiasSemana(null);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaFake()));
        assertThatThrownBy(() -> tarefaService.salvar(request)).isInstanceOf(DiasSemanaObrigatorio.class);
        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void eventoSemHoraInicio() {
        TarefaRequest request = requestValido();
        request.setTipo(TipoTarefa.EVENTO);
        request.setHoraInicio(null);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaFake()));
        assertThatThrownBy(() -> tarefaService.salvar(request)).isInstanceOf(HorarioObrigatorio.class);
        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void buscarIdTarefaComSucesso() {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setNome("Reunião");
        tarefa.setCategoria(categoriaFake());
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        TarefaResponse resultado = tarefaService.buscarPorId(1L);
        assertThat(resultado.getNome()).isEqualTo("Reunião");
    }

    @Test
    void buscarIdTarefaInexistente() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tarefaService.buscarPorId(99L)).isInstanceOf(TarefaNaoEncontrada.class);
    }

    @Test
    void deletarIdTarefaComSucesso() {
        when(tarefaRepository.existsById(1L)).thenReturn(true);
        tarefaService.deletar(1L);
        verify(tarefaRepository).deleteById(1L);
    }

    @Test
    void deletarIdTarefaInexistente() {
        when(tarefaRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> tarefaService.deletar(99L)).isInstanceOf(TarefaNaoEncontrada.class);
        verify(tarefaRepository, never()).deleteById(any());
    }

    @Test
    void listarTodasTarefas() {
        Tarefa tarefa1 = new Tarefa();
        tarefa1.setId(1L);
        tarefa1.setNome("Tarefa 1");
        tarefa1.setCategoria(categoriaFake());
        Tarefa tarefa2 = new Tarefa();
        tarefa2.setId(2L);
        tarefa2.setNome("Tarefa 2");
        tarefa2.setCategoria(categoriaFake());
        when(tarefaRepository.findAll()).thenReturn(List.of(tarefa1, tarefa2));
        List<TarefaResponse> resultado = tarefaService.listarTodas();
        assertThat(resultado).hasSize(2);
    }

    @Test
    void salvarTarefaSemanalComDiasSemana() {
        TarefaRequest request = requestValido();
        request.setRecorrencia(Recorrencia.SEMANAL);
        request.setDiasSemana("SEGUNDA,QUARTA");
        Tarefa tarefaSalva = new Tarefa();
        tarefaSalva.setId(10L);
        tarefaSalva.setCategoria(categoriaFake());
        tarefaSalva.setDiasSemana("SEGUNDA,QUARTA");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaFake()));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefaSalva);
        TarefaResponse resultado = tarefaService.salvar(request);
        assertThat(resultado.getDiasSemana()).isEqualTo("SEGUNDA,QUARTA");
    }

    @Test
    void salvarEventoComHoraInicio() {
        TarefaRequest request = requestValido();
        request.setTipo(TipoTarefa.EVENTO);
        request.setHoraInicio(LocalTime.of(14, 30));
        Tarefa tarefaSalva = new Tarefa();
        tarefaSalva.setId(10L);
        tarefaSalva.setCategoria(categoriaFake());
        tarefaSalva.setTipo(TipoTarefa.EVENTO);
        tarefaSalva.setHoraInicio(LocalTime.of(14, 30));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaFake()));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefaSalva);
        TarefaResponse resultado = tarefaService.salvar(request);
        assertThat(resultado.getHoraInicio()).isEqualTo(LocalTime.of(14, 30));
    }
}