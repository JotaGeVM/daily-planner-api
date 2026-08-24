package io.github.jotagevm.daily_planner_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jotagevm.daily_planner_api.model.Recorrencia;
import io.github.jotagevm.daily_planner_api.model.Tarefa;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByNomeContainingIgnoreCase(String nome);

    List<Tarefa> findByCategoria_NomeContainingIgnoreCase(String nomeCategoria);

    List<Tarefa> findByCategoriaId(Long categoriaId);

    List<Tarefa> findByRecorrencia(Recorrencia recorrencia);
}