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
import io.github.jotagevm.daily_planner_api.model.Usuario;
import io.github.jotagevm.daily_planner_api.repository.CategoriaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private TarefaRepository tarefaRepository;
    @Mock
    private UsuarioAtualProvider usuarioAtualProvider;
    @InjectMocks
    private CategoriaService categoriaService;

    private Usuario usuarioFake() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@exemplo.com");
        return usuario;
    }

    @Test
    void criarCategoriaComSucesso() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Trabalho");
        request.setCorHex("#6366f1");
        Categoria categoriaSalva = new Categoria();
        categoriaSalva.setId(1L);
        categoriaSalva.setNome("Trabalho");
        categoriaSalva.setCorHex("#6366f1");
        when(categoriaRepository.existsByNomeAndUsuarioId("Trabalho", 1L)).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);
        CategoriaResponse resultado = categoriaService.criar(request);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Trabalho");
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void criarCategoriaComNomeDuplicado() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Trabalho");
        when(categoriaRepository.existsByNomeAndUsuarioId("Trabalho", 1L)).thenReturn(true);
        assertThatThrownBy(() -> categoriaService.criar(request)).isInstanceOf(CategoriaJaCadastrada.class)
                .hasMessageContaining("Trabalho");
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void deletarIdCategoriaComSucesso() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoria));
        when(tarefaRepository.findByCategoriaIdAndUsuarioId(1L, 1L)).thenReturn(List.of());
        categoriaService.deletar(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void deletarIdCategoriaInexistente() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        when(categoriaRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoriaService.deletar(99L)).isInstanceOf(CategoriaNaoEncontrada.class);
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void deletarCategoriaEmUso() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoria));
        when(tarefaRepository.findByCategoriaIdAndUsuarioId(1L, 1L))
                .thenReturn(List.of(new io.github.jotagevm.daily_planner_api.model.Tarefa()));
        assertThatThrownBy(() -> categoriaService.deletar(1L)).isInstanceOf(CategoriaEmUso.class);
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void listarTodasAsCategorias() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Categoria categoria1 = new Categoria();
        categoria1.setId(1L);
        categoria1.setNome("Trabalho");
        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNome("Pessoal");
        Page<Categoria> pagina = new PageImpl<>(List.of(categoria1, categoria2));
        when(categoriaRepository.findByUsuarioId(eq(1L), any(Pageable.class))).thenReturn(pagina);
        Page<CategoriaResponse> resultado = categoriaService.listarTodas(PageRequest.of(0, 50));
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Trabalho");
    }

    @Test
    void buscarIdCategoriaComSucesso() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Trabalho");
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoria));
        CategoriaResponse resultado = categoriaService.buscarPorId(1L);
        assertThat(resultado.getNome()).isEqualTo("Trabalho");
    }

    @Test
    void buscarIdCategoriaInexistente() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        when(categoriaRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoriaService.buscarPorId(99L)).isInstanceOf(CategoriaNaoEncontrada.class);
    }

    @Test
    void atualizarIdCategoriaComSucesso() {
        when(usuarioAtualProvider.obterUsuarioAtual()).thenReturn(usuarioFake());
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(1L);
        categoriaExistente.setNome("Trabalho");
        CategoriaRequest request = new CategoriaRequest();
        request.setNome("Trabalho Atualizado");
        when(categoriaRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaExistente);
        categoriaService.atualizar(1L, request);
        verify(categoriaRepository).save(categoriaExistente);
        assertThat(categoriaExistente.getNome()).isEqualTo("Trabalho Atualizado");
    }
}