package io.github.jotagevm.daily_planner_api.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jotagevm.daily_planner_api.model.Ocorrencia;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    List<Ocorrencia> findByDataHora(LocalDate dataHora);

    List<Ocorrencia> findByTarefaId(Long tarefaId);

    boolean existsByTarefaIdAndDataHora(Long tarefaId, LocalDate dataHora);

    List<Ocorrencia> findByTarefa_UsuarioId(Long usuarioId);

    List<Ocorrencia> findByTarefaIdAndTarefa_UsuarioId(Long tarefaId, Long usuarioId);

    Optional<Ocorrencia> findByIdAndTarefa_UsuarioId(Long id, Long usuarioId);
}