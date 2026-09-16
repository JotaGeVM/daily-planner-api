package io.github.jotagevm.daily_planner_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jotagevm.daily_planner_api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNome(String nome);

    boolean existsByNomeAndUsuarioId(String nome, Long usuarioId);

    List<Categoria> findByNomeContainingIgnoreCase(String nome);

    List<Categoria> findByUsuarioId(Long usuarioId);

    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);
}