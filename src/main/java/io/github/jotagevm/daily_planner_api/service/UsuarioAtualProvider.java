package io.github.jotagevm.daily_planner_api.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.github.jotagevm.daily_planner_api.model.Usuario;
import io.github.jotagevm.daily_planner_api.repository.UsuarioRepository;

@Component
public class UsuarioAtualProvider {
    private final UsuarioRepository usuarioRepository;

    public UsuarioAtualProvider(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obterUsuarioAtual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado: " + email));
    }
}