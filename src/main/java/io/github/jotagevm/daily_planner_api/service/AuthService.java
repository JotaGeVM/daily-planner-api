package io.github.jotagevm.daily_planner_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.jotagevm.daily_planner_api.dto.AuthResponse;
import io.github.jotagevm.daily_planner_api.dto.LoginRequest;
import io.github.jotagevm.daily_planner_api.dto.RegistroRequest;
import io.github.jotagevm.daily_planner_api.exception.CredenciaisInvalidas;
import io.github.jotagevm.daily_planner_api.exception.EmailJaCadastrado;
import io.github.jotagevm.daily_planner_api.model.Usuario;
import io.github.jotagevm.daily_planner_api.repository.UsuarioRepository;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse registrar(RegistroRequest dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastrado("Email '" + dto.getEmail() + "' já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario.getEmail());
        return new AuthResponse(token, usuario.getEmail());
    }

    public AuthResponse login(LoginRequest dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CredenciaisInvalidas("Email ou senha inválidos"));

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new CredenciaisInvalidas("Email ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario.getEmail());
        return new AuthResponse(token, usuario.getEmail());
    }
}