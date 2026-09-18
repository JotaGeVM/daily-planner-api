package io.github.jotagevm.daily_planner_api.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.jotagevm.daily_planner_api.model.Tarefa;

@Service
public class RecorrenciaService {

    public List<LocalDate> expandir(Tarefa tarefa, LocalDate inicio, LocalDate fim) {
        List<LocalDate> datas = new ArrayList<>();
        LocalDate dataInicio = tarefa.getDataInicio();
        if (dataInicio == null || dataInicio.isAfter(fim)) {
            return datas;
        }

        LocalDate cursor = dataInicio.isAfter(inicio) ? dataInicio : inicio;

        switch (tarefa.getRecorrencia()) {
            case NENHUMA -> {
                if (!dataInicio.isBefore(inicio) && !dataInicio.isAfter(fim)) {
                    datas.add(dataInicio);
                }
            }
            case DIARIA -> {
                while (!cursor.isAfter(fim)) {
                    datas.add(cursor);
                    cursor = cursor.plusDays(1);
                }
            }
            case SEMANAL -> {
                Set<DayOfWeek> dias = parseDiasSemana(tarefa.getDiasSemana());
                while (!cursor.isAfter(fim)) {
                    if (dias.contains(cursor.getDayOfWeek())) {
                        datas.add(cursor);
                    }
                    cursor = cursor.plusDays(1);
                }
            }
            case QUINZENAL -> {
                Set<DayOfWeek> dias = parseDiasSemana(tarefa.getDiasSemana());
                LocalDate inicioSemanaBase = dataInicio.with(DayOfWeek.MONDAY);
                while (!cursor.isAfter(fim)) {
                    if (dias.contains(cursor.getDayOfWeek())) {
                        LocalDate inicioSemanaCursor = cursor.with(DayOfWeek.MONDAY);
                        long semanasDesdeInicio = ChronoUnit.WEEKS.between(inicioSemanaBase, inicioSemanaCursor);
                        if (semanasDesdeInicio % 2 == 0) {
                            datas.add(cursor);
                        }
                    }
                    cursor = cursor.plusDays(1);
                }
            }
            case MENSAL -> {
                int diaDoMes = dataInicio.getDayOfMonth();
                while (!cursor.isAfter(fim)) {
                    int diaEsperado = Math.min(diaDoMes, cursor.lengthOfMonth());
                    if (cursor.getDayOfMonth() == diaEsperado) {
                        datas.add(cursor);
                    }
                    cursor = cursor.plusDays(1);
                }
            }
        }

        return datas;
    }

    private Set<DayOfWeek> parseDiasSemana(String diasSemana) {
        if (diasSemana == null || diasSemana.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(diasSemana.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(this::normalizar)
                .map(this::paraDayOfWeek)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private String normalizar(String dia) {
        return dia
                .replace("Á", "A").replace("Ã", "A").replace("Â", "A")
                .replace("É", "E").replace("Ê", "E")
                .replace("Í", "I")
                .replace("Ó", "O").replace("Ô", "O").replace("Õ", "O")
                .replace("Ú", "U")
                .replace("Ç", "C");
    }

    private DayOfWeek paraDayOfWeek(String dia) {
        return switch (dia) {
            case "SEGUNDA" -> DayOfWeek.MONDAY;
            case "TERCA" -> DayOfWeek.TUESDAY;
            case "QUARTA" -> DayOfWeek.WEDNESDAY;
            case "QUINTA" -> DayOfWeek.THURSDAY;
            case "SEXTA" -> DayOfWeek.FRIDAY;
            case "SABADO" -> DayOfWeek.SATURDAY;
            case "DOMINGO" -> DayOfWeek.SUNDAY;
            default -> null;
        };
    }
}