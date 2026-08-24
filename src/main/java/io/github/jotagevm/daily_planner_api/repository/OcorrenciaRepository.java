package io.github.jotagevm.daily_planner_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jotagevm.daily_planner_api.model.Ocorrencia;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    List<Ocorrencia> findByData(LocalDate data);

    List<Ocorrencia> findByTarefaId(Long tarefaId);

    boolean existsByTarefaIdAndData(Long tarefaId, LocalDate data);
}
