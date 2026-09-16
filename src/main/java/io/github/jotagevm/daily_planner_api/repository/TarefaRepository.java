package io.github.jotagevm.daily_planner_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jotagevm.daily_planner_api.model.Recorrencia;
import io.github.jotagevm.daily_planner_api.model.Tarefa;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByNomeContainingIgnoreCase(String nome);

    List<Tarefa> findByCategoria_NomeContainingIgnoreCase(String nomeCategoria);

    List<Tarefa> findByCategoriaId(Long categoriaId);

    List<Tarefa> findByCategoriaIdAndUsuarioId(Long categoriaId, Long usuarioId);

    List<Tarefa> findByRecorrencia(Recorrencia recorrencia);

    List<Tarefa> findByUsuarioId(Long usuarioId);

    Optional<Tarefa> findByIdAndUsuarioId(Long id, Long usuarioId);
}