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
import io.github.jotagevm.daily_planner_api.repository.OcorrenciaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;
import io.github.jotagevm.daily_planner_api.repository.OcorrenciaRepository;
import io.github.jotagevm.daily_planner_api.dto.HabitoStreakResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {
    @Mock
    private TarefaRepository tarefaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private UsuarioAtualProvider usuarioAtualProvider;
    @InjectMocks
    private TarefaService tarefaService;
    @Mock
    private OcorrenciaRepository ocorrenciaRepository;

    private Usuario usuarioFake() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@exemplo.com");
        return usuario;
    }

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
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        TarefaRequest request = requestValido();
        Categoria categoria = categoriaFake();
        Tarefa tarefaSalva = new Tarefa();
        tarefaSalva.setId(10L);
        tarefaSalva.setNome("Reunião");
        tarefaSalva.setDescricao("Reunião semanal do time");
        tarefaSalva.setCategoria(categoria);
        tarefaSalva.setTipo(TipoTarefa.TAREFA);
        tarefaSalva.setRecorrencia(Recorrencia.NENHUMA);
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoria));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefaSalva);
        TarefaResponse resultado = tarefaService.salvar(request);
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getCategoriaNome()).isEqualTo("Trabalho");
    }

    @Test
    void idCategoriaNaoEncontrada() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        TarefaRequest request = requestValido();
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tarefaService.salvar(request)).isInstanceOf(CategoriaNaoEncontrada.class);
        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void recorrenciaSemanalSemDiasSemana() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        TarefaRequest request = requestValido();
        request.setRecorrencia(Recorrencia.SEMANAL);
        request.setDiasSemana(null);
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoriaFake()));
        assertThatThrownBy(() -> tarefaService.salvar(request)).isInstanceOf(DiasSemanaObrigatorio.class);
        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void eventoSemHoraInicio() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        TarefaRequest request = requestValido();
        request.setTipo(TipoTarefa.EVENTO);
        request.setHoraInicio(null);
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoriaFake()));
        assertThatThrownBy(() -> tarefaService.salvar(request)).isInstanceOf(HorarioObrigatorio.class);
        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void buscarIdTarefaComSucesso() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setNome("Reunião");
        tarefa.setCategoria(categoriaFake());
        when(tarefaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(tarefa));
        TarefaResponse resultado = tarefaService.buscarPorId(1L);
        assertThat(resultado.getNome()).isEqualTo("Reunião");
    }

    @Test
    void buscarIdTarefaInexistente() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        when(tarefaRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tarefaService.buscarPorId(99L)).isInstanceOf(TarefaNaoEncontrada.class);
    }

    @Test
    void deletarIdTarefaComSucesso() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        when(tarefaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(tarefa));
        tarefaService.deletar(1L);
        verify(tarefaRepository).deleteById(1L);
    }

    @Test
    void deletarIdTarefaInexistente() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        when(tarefaRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> tarefaService.deletar(99L)).isInstanceOf(TarefaNaoEncontrada.class);
        verify(tarefaRepository, never()).deleteById(any());
    }

    @Test
    void listarTodasTarefas() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Tarefa tarefa1 = new Tarefa();
        tarefa1.setId(1L);
        tarefa1.setNome("Tarefa 1");
        tarefa1.setCategoria(categoriaFake());
        Tarefa tarefa2 = new Tarefa();
        tarefa2.setId(2L);
        tarefa2.setNome("Tarefa 2");
        tarefa2.setCategoria(categoriaFake());
        Page<Tarefa> pagina = new PageImpl<>(List.of(tarefa1, tarefa2));
        when(tarefaRepository.findByUsuarioId(eq(1L), any(Pageable.class))).thenReturn(pagina);
        Page<TarefaResponse> resultado = tarefaService.listarTodas(PageRequest.of(0, 50));
        assertThat(resultado.getContent()).hasSize(2);
    }

    @Test
    void salvarTarefaSemanalComDiasSemana() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        TarefaRequest request = requestValido();
        request.setRecorrencia(Recorrencia.SEMANAL);
        request.setDiasSemana("SEGUNDA,QUARTA");
        Tarefa tarefaSalva = new Tarefa();
        tarefaSalva.setId(10L);
        tarefaSalva.setCategoria(categoriaFake());
        tarefaSalva.setDiasSemana("SEGUNDA,QUARTA");
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoriaFake()));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefaSalva);
        TarefaResponse resultado = tarefaService.salvar(request);
        assertThat(resultado.getDiasSemana()).isEqualTo("SEGUNDA,QUARTA");
    }

    @Test
    void salvarEventoComHoraInicio() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        TarefaRequest request = requestValido();
        request.setTipo(TipoTarefa.EVENTO);
        request.setHoraInicio(LocalTime.of(14, 30));
        Tarefa tarefaSalva = new Tarefa();
        tarefaSalva.setId(10L);
        tarefaSalva.setCategoria(categoriaFake());
        tarefaSalva.setTipo(TipoTarefa.EVENTO);
        tarefaSalva.setHoraInicio(LocalTime.of(14, 30));
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoriaFake()));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefaSalva);
        TarefaResponse resultado = tarefaService.salvar(request);
        assertThat(resultado.getHoraInicio()).isEqualTo(LocalTime.of(14, 30));
    }

    @Test
    void streakDeTarefaQueNaoEhHabito() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTipo(TipoTarefa.TAREFA);
        when(tarefaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(tarefa));

        HabitoStreakResponse resultado = tarefaService.calcularStreak(1L);

        assertThat(resultado.getStreakAtual()).isEqualTo(0);
        assertThat(resultado.getMelhorStreak()).isEqualTo(0);
    }

    @Test
    void streakSemNenhumaOcorrencia() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTipo(TipoTarefa.HABITO);
        tarefa.setMetaDiaria(3);
        when(tarefaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(tarefa));
        when(ocorrenciaRepository.findByTarefaIdAndTarefa_UsuarioId(1L, 1L)).thenReturn(List.of());

        HabitoStreakResponse resultado = tarefaService.calcularStreak(1L);

        assertThat(resultado.getStreakAtual()).isEqualTo(0);
        assertThat(resultado.getMelhorStreak()).isEqualTo(0);
    }

    @Test
    void streakAtualDeTresDiasConsecutivosAteHoje() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTipo(TipoTarefa.HABITO);
        tarefa.setMetaDiaria(2);
        when(tarefaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(tarefa));

        java.time.LocalDate hoje = java.time.LocalDate.now();
        List<Ocorrencia> ocorrencias = new java.util.ArrayList<>();
        for (int diasAtras = 0; diasAtras < 3; diasAtras++) {
            java.time.LocalDate dia = hoje.minusDays(diasAtras);
            for (int i = 0; i < 2; i++) {
                Ocorrencia o = new Ocorrencia();
                o.setDataHora(LocalDateTime.of(dia, java.time.LocalTime.of(10, i)));
                ocorrencias.add(o);
            }
        }
        when(ocorrenciaRepository.findByTarefaIdAndTarefa_UsuarioId(1L, 1L)).thenReturn(ocorrencias);

        HabitoStreakResponse resultado = tarefaService.calcularStreak(1L);

        assertThat(resultado.getStreakAtual()).isEqualTo(3);
        assertThat(resultado.getMelhorStreak()).isEqualTo(3);
    }

    @Test
    void melhorStreakMaiorQueOAtualQuandoSequenciaFoiQuebrada() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTipo(TipoTarefa.HABITO);
        tarefa.setMetaDiaria(1);
        when(tarefaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(tarefa));

        java.time.LocalDate hoje = java.time.LocalDate.now();
        List<Ocorrencia> ocorrencias = new java.util.ArrayList<>();
        // sequência antiga de 5 dias, de 10 a 6 dias atrás (quebrada, não chega até
        // hoje)
        for (int diasAtras = 10; diasAtras >= 6; diasAtras--) {
            Ocorrencia o = new Ocorrencia();
            o.setDataHora(LocalDateTime.of(hoje.minusDays(diasAtras), java.time.LocalTime.of(10, 0)));
            ocorrencias.add(o);
        }
        // sequência atual de só 2 dias, terminando hoje
        for (int diasAtras = 1; diasAtras >= 0; diasAtras--) {
            Ocorrencia o = new Ocorrencia();
            o.setDataHora(LocalDateTime.of(hoje.minusDays(diasAtras), java.time.LocalTime.of(10, 0)));
            ocorrencias.add(o);
        }
        when(ocorrenciaRepository.findByTarefaIdAndTarefa_UsuarioId(1L, 1L)).thenReturn(ocorrencias);

        HabitoStreakResponse resultado = tarefaService.calcularStreak(1L);

        assertThat(resultado.getStreakAtual()).isEqualTo(2);
        assertThat(resultado.getMelhorStreak()).isEqualTo(5);
    }
}