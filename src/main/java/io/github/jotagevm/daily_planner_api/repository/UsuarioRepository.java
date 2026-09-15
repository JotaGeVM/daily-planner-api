package io.github.jotagevm.daily_planner_api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github.jotagevm.daily_planner_api.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}