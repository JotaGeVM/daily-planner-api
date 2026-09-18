package io.github.jotagevm.daily_planner_api.model;

import java.time.LocalTime;
import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Tarefas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    private TipoTarefa tipo;

    @Enumerated(EnumType.STRING)
    private Recorrencia recorrencia;

    private String diasSemana;

    @Column(nullable = true)
    private LocalTime horaInicio;

    @Column(nullable = true)
    private Integer duracao;

    private Integer metaDiaria;

    private LocalDate dataInicio;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}