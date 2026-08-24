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
import io.github.jotagevm.daily_planner_api.repository.CategoriaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;

@Service
public class CategoriaService {
    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, TarefaRepository tarefaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.tarefaRepository = tarefaRepository;
    }

    private void preencherCampos(Categoria categoria, CategoriaRequest dto) {
        categoria.setNome(dto.getNome());
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
        dto.setCorHex(categoria.getCorHex());

        return dto;
    }

    public CategoriaResponse criar(CategoriaRequest dto) {
        if (categoriaRepository.existsByNome(dto.getNome())) {
            throw new CategoriaJaCadastrada("Categoria com nome '" + dto.getNome() + "' já cadastrada");
        }
        Categoria categoria = categoriaRepository.save(converterEntidade(dto));
        return converterDto(categoria);
    }

    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::converterDto)
                .collect(Collectors.toList());
    }

    public CategoriaResponse buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::converterDto)
                .orElseThrow(() -> new CategoriaNaoEncontrada("Categoria de ID " + id + " não encontrada"));
    }

    public CategoriaResponse atualizar(Long id, CategoriaRequest categoriaAtualizada) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontrada("Categoria de ID " + id + " não encontrada"));
        preencherCampos(categoria, categoriaAtualizada);

        return converterDto(categoriaRepository.save(categoria));
    }

    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNaoEncontrada("Categoria de ID " + id + " não encontrada");
        }
        if (!tarefaRepository.findByCategoriaId(id).isEmpty()) {
            throw new CategoriaEmUso("Categoria de ID " + id + " possui tarefas vinculadas e não pode ser removida");
        }
        categoriaRepository.deleteById(id);
    }
}
