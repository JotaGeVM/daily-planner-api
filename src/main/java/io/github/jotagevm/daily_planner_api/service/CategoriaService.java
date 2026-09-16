package io.github.jotagevm.daily_planner_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.jotagevm.daily_planner_api.dto.CategoriaRequest;
import io.github.jotagevm.daily_planner_api.dto.CategoriaResponse;
import io.github.jotagevm.daily_planner_api.exception.CategoriaEmUso;
import io.github.jotagevm.daily_planner_api.exception.CategoriaJaCadastrada;
import io.github.jotagevm.daily_planner_api.exception.CategoriaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.model.Categoria;
import io.github.jotagevm.daily_planner_api.model.Usuario;
import io.github.jotagevm.daily_planner_api.repository.CategoriaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;

@Service
public class CategoriaService {
    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioAtualProvider usuarioAtualProvider;

    public CategoriaService(CategoriaRepository categoriaRepository, TarefaRepository tarefaRepository,
            UsuarioAtualProvider usuarioAtualProvider) {
        this.categoriaRepository = categoriaRepository;
        this.tarefaRepository = tarefaRepository;
        this.usuarioAtualProvider = usuarioAtualProvider;
    }

    private void preencherCampos(Categoria categoria, CategoriaRequest dto) {
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setCorHex(dto.getCorHex());
    }

    private Categoria converterEntidade(CategoriaRequest dto) {
        Categoria categoria = new Categoria();
        preencherCampos(categoria, dto);
        return categoria;
    }

    private CategoriaResponse converterDto(Categoria categoria) {
        CategoriaResponse dto = new CategoriaResponse();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        dto.setCorHex(categoria.getCorHex());

        return dto;
    }

    public CategoriaResponse criar(CategoriaRequest dto) {
        Usuario usuario = usuarioAtualProvider.obterUsuarioAtual();

        if (categoriaRepository.existsByNomeAndUsuarioId(dto.getNome(), usuario.getId())) {
            throw new CategoriaJaCadastrada("Categoria com nome '" + dto.getNome() + "' já cadastrada");
        }
        Categoria categoria = converterEntidade(dto);
        categoria.setUsuario(usuario);
        return converterDto(categoriaRepository.save(categoria));
    }

    public List<CategoriaResponse> listarTodas() {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        return categoriaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::converterDto)
                .collect(Collectors.toList());
    }

    public CategoriaResponse buscarPorId(Long id) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        return categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .map(this::converterDto)
                .orElseThrow(() -> new CategoriaNaoEncontrada("Categoria de ID " + id + " não encontrada"));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest categoriaAtualizada) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new CategoriaNaoEncontrada("Categoria de ID " + id + " não encontrada"));
        preencherCampos(categoria, categoriaAtualizada);

        return converterDto(categoriaRepository.save(categoria));
    }

    public void deletar(Long id) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new CategoriaNaoEncontrada("Categoria de ID " + id + " não encontrada"));

        if (!tarefaRepository.findByCategoriaIdAndUsuarioId(id, usuarioId).isEmpty()) {
            throw new CategoriaEmUso("Categoria de ID " + id + " possui tarefas vinculadas e não pode ser removida");
        }
        categoriaRepository.deleteById(id);
    }
}