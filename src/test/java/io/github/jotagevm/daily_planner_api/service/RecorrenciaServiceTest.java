package io.github.jotagevm.daily_planner_api.service;

import org.junit.jupiter.api.Test;

import io.github.jotagevm.daily_planner_api.model.Recorrencia;
import io.github.jotagevm.daily_planner_api.model.Tarefa;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecorrenciaServiceTest {

    private final RecorrenciaService service = new RecorrenciaService();

    private Tarefa tarefaComRecorrencia(Recorrencia recorrencia, LocalDate dataInicio, String diasSemana) {
        Tarefa tarefa = new Tarefa();
        tarefa.setRecorrencia(recorrencia);
        tarefa.setDataInicio(dataInicio);
        tarefa.setDiasSemana(diasSemana);
        return tarefa;
    }

    @Test
    void nenhumaDentroDoIntervaloRetornaUmaData() {
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.NENHUMA, LocalDate.of(2026, 9, 20), null);

        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertThat(datas).containsExactly(LocalDate.of(2026, 9, 20));
    }

    @Test
    void nenhumaForaDoIntervaloRetornaVazio() {
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.NENHUMA, LocalDate.of(2026, 10, 5), null);

        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertThat(datas).isEmpty();
    }

    @Test
    void diariaRetornaTodasAsDatasDoIntervalo() {
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.DIARIA, LocalDate.of(2026, 9, 1), null);

        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));

        assertThat(datas).containsExactly(
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 2),
                LocalDate.of(2026, 9, 3),
                LocalDate.of(2026, 9, 4),
                LocalDate.of(2026, 9, 5));
    }

    @Test
    void diariaRespeitaDataInicioPosteriorAoInicioDoIntervalo() {
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.DIARIA, LocalDate.of(2026, 9, 3), null);

        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));

        assertThat(datas).containsExactly(
                LocalDate.of(2026, 9, 3),
                LocalDate.of(2026, 9, 4),
                LocalDate.of(2026, 9, 5));
    }

    @Test
    void semanalRetornaSoOsDiasDaSemanaEscolhidos() {
        // 2026-09-01 é uma terça-feira
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.SEMANAL, LocalDate.of(2026, 9, 1), "SEGUNDA,QUARTA");

        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 14));

        assertThat(datas).containsExactly(
                LocalDate.of(2026, 9, 2), // quarta
                LocalDate.of(2026, 9, 7), // segunda
                LocalDate.of(2026, 9, 9), // quarta
                LocalDate.of(2026, 9, 14)); // segunda
    }

    @Test
    void quinzenalSoConsideraSemanasAlternadas() {
        // 2026-09-01 (terça) está na "semana base"; SEGUNDA cai em 2026-08-31 (antes do
        // início real, mas usada só como referência de paridade)
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.QUINZENAL, LocalDate.of(2026, 9, 1), "SEGUNDA");

        // 4 semanas = 4 segundas-feiras candidatas: 07/09, 14/09, 21/09, 28/09
        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertThat(datas).containsExactly(
                LocalDate.of(2026, 9, 14),
                LocalDate.of(2026, 9, 28));
    }

    @Test
    void mensalUsaOMesmoDiaDoMes() {
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.MENSAL, LocalDate.of(2026, 9, 15), null);

        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 11, 30));

        assertThat(datas).containsExactly(
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 11, 15));
    }

    @Test
    void mensalEncaixaNoUltimoDiaQuandoOMesEMenor() {
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.MENSAL, LocalDate.of(2026, 1, 31), null);

        // fevereiro de 2026 tem 28 dias (não é bissexto)
        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));

        assertThat(datas).containsExactly(LocalDate.of(2026, 2, 28));
    }

    @Test
    void dataInicioNulaRetornaVazio() {
        Tarefa tarefa = tarefaComRecorrencia(Recorrencia.DIARIA, null, null);

        List<LocalDate> datas = service.expandir(tarefa, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));

        assertThat(datas).isEmpty();
    }
}