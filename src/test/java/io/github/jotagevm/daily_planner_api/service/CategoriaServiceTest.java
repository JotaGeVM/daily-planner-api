package io.github.jotagevm.daily_planner_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.github.jotagevm.daily_planner_api.dto.CategoriaRequest;
import io.github.jotagevm.daily_planner_api.dto.CategoriaResponse;
import io.github.jotagevm.daily_planner_api.exception.CategoriaJaCadastrada;
import io.github.jotagevm.daily_planner_api.exception.CategoriaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.exception.CategoriaEmUso;
import io.github.jotagevm.daily_planner_api.model.Categoria;
import io.github.jotagevm.daily_planner_api.repository.CategoriaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private TarefaRepository tarefaRepository;
    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void criarCategoriaComSucesso() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Trabalho");
        request.setCorHex("#6366f1");
        Categoria categoriaSalva = new Categoria();
        categoriaSalva.setId(1L);
        categoriaSalva.setNome("Trabalho");
        categoriaSalva.setCorHex("#6366f1");
        when(categoriaRepository.existsByNome("Trabalho")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);
        CategoriaResponse resultado = categoriaService.criar(request);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Trabalho");
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void criarCategoriaComNomeDuplicado() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Trabalho");
        when(categoriaRepository.existsByNome("Trabalho")).thenReturn(true);
        assertThatThrownBy(() -> categoriaService.criar(request)).isInstanceOf(CategoriaJaCadastrada.class)
                .hasMessageContaining("Trabalho");
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void deletarIdCategoriaComSucesso() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(tarefaRepository.findByCategoriaId(1L)).thenReturn(List.of());
        categoriaService.deletar(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void deletarIdCategoriaInexistente() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> categoriaService.deletar(99L)).isInstanceOf(CategoriaNaoEncontrada.class);
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void deletarCategoriaEmUso() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(tarefaRepository.findByCategoriaId(1L))
                .thenReturn(List.of(new io.github.jotagevm.daily_planner_api.model.Tarefa()));
        assertThatThrownBy(() -> categoriaService.deletar(1L)).isInstanceOf(CategoriaEmUso.class);
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void listarTodasAsCategorias() {
        Categoria categoria1 = new Categoria();
        categoria1.setId(1L);
        categoria1.setNome("Trabalho");
        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNome("Pessoal");
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria1, categoria2));
        List<CategoriaResponse> resultado = categoriaService.listarTodas();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("Trabalho");
    }

    @Test
    void buscarIdCategoriaComSucesso() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Trabalho");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        CategoriaResponse resultado = categoriaService.buscarPorId(1L);
        assertThat(resultado.getNome()).isEqualTo("Trabalho");
    }

    @Test
    void buscarIdCategoriaInexistente() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoriaService.buscarPorId(99L)).isInstanceOf(CategoriaNaoEncontrada.class);
    }

    @Test
    void atualizarIdCategoriaComSucesso() {
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(1L);
        categoriaExistente.setNome("Trabalho");
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Trabalho Atualizado");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaExistente);
        categoriaService.atualizar(1L, request);
        verify(categoriaRepository).save(categoriaExistente);
        assertThat(categoriaExistente.getNome()).isEqualTo("Trabalho Atualizado");
    }
}