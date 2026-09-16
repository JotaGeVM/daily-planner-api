package io.github.jotagevm.daily_planner_api.service;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.jotagevm.daily_planner_api.dto.TarefaRequest;
import io.github.jotagevm.daily_planner_api.dto.TarefaResponse;
import io.github.jotagevm.daily_planner_api.exception.CategoriaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.exception.DiasSemanaObrigatorio;
import io.github.jotagevm.daily_planner_api.exception.HorarioObrigatorio;
import io.github.jotagevm.daily_planner_api.exception.TarefaNaoEncontrada;
import io.github.jotagevm.daily_planner_api.model.Categoria;
import io.github.jotagevm.daily_planner_api.model.Recorrencia;
import io.github.jotagevm.daily_planner_api.model.Tarefa;
import io.github.jotagevm.daily_planner_api.model.TipoTarefa;
import io.github.jotagevm.daily_planner_api.model.Usuario;
import io.github.jotagevm.daily_planner_api.repository.CategoriaRepository;
import io.github.jotagevm.daily_planner_api.repository.TarefaRepository;

@Service
public class TarefaService {
    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioAtualProvider usuarioAtualProvider;

    public TarefaService(TarefaRepository tarefaRepository, CategoriaRepository categoriaRepository,
            UsuarioAtualProvider usuarioAtualProvider) {
        this.tarefaRepository = tarefaRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioAtualProvider = usuarioAtualProvider;
    }

    private void preencherCampos(Tarefa tarefa, TarefaRequest dto, Long usuarioId) {
        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(dto.getCategoriaId(), usuarioId)
                .orElseThrow(() -> new CategoriaNaoEncontrada(
                        "Categoria de ID " + dto.getCategoriaId() + " não encontrada"));
        tarefa.setNome(dto.getNome());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setCategoria(categoria);
        tarefa.setTipo(dto.getTipo());
        tarefa.setRecorrencia(dto.getRecorrencia());
        if (dto.getRecorrencia() == Recorrencia.SEMANAL
                && (dto.getDiasSemana() == null || dto.getDiasSemana().isBlank())) {
            throw new DiasSemanaObrigatorio("Dias da semana obrigatórios para recorrência semanal");
        } else {
            tarefa.setDiasSemana(dto.getDiasSemana());
        }
        if (dto.getTipo() == TipoTarefa.EVENTO && dto.getHoraInicio() == null) {
            throw new HorarioObrigatorio("Horário de início obrigatório para tarefa do tipo evento");
        } else {
            tarefa.setHoraInicio(dto.getHoraInicio());
        }
        tarefa.setDuracao(dto.getDuracao());
    }

    private TarefaResponse converterDto(Tarefa tarefa) {
        TarefaResponse dto = new TarefaResponse();
        dto.setId(tarefa.getId());
        dto.setNome(tarefa.getNome());
        dto.setDescricao(tarefa.getDescricao());
        dto.setCategoriaId(tarefa.getCategoria().getId());
        dto.setCategoriaNome(tarefa.getCategoria().getNome());
        dto.setTipo(tarefa.getTipo());
        dto.setRecorrencia(tarefa.getRecorrencia());
        dto.setDiasSemana(tarefa.getDiasSemana());
        dto.setHoraInicio(tarefa.getHoraInicio());
        dto.setDuracao(tarefa.getDuracao());

        return dto;
    }

    public TarefaResponse salvar(TarefaRequest dto) {
        Usuario usuario = usuarioAtualProvider.obterUsuarioAtual();
        Tarefa tarefa = new Tarefa();
        preencherCampos(tarefa, dto, usuario.getId());
        tarefa.setUsuario(usuario);
        return converterDto(tarefaRepository.save(tarefa));
    }

    public Page<TarefaResponse> listarTodas(Pageable pageable) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        return tarefaRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::converterDto);
    }

    public TarefaResponse buscarPorId(Long id) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        return tarefaRepository.findByIdAndUsuarioId(id, usuarioId)
                .map(this::converterDto)
                .orElseThrow(() -> new TarefaNaoEncontrada("Tarefa de ID " + id + " não encontrada"));
    }

    public TarefaResponse atualizar(Long id, TarefaRequest tarefaAtualizada) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        Tarefa tarefa = tarefaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new TarefaNaoEncontrada("Tarefa de ID " + id + " não encontrada"));
        preencherCampos(tarefa, tarefaAtualizada, usuarioId);

        return converterDto(tarefaRepository.save(tarefa));
    }

    public void deletar(Long id) {
        Long usuarioId = usuarioAtualProvider.obterUsuarioAtual().getId();
        tarefaRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new TarefaNaoEncontrada("Tarefa de ID " + id + " não encontrada"));
        tarefaRepository.deleteById(id);
    }
}