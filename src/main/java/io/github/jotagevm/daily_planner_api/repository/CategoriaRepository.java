package io.github.jotagevm.daily_planner_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.jotagevm.daily_planner_api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNome(String nome);

    List<Categoria> findByNomeContainingIgnoreCase(String nome);
}
