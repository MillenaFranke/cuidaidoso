package br.com.fiap.cuidaidoso.dto;

import java.time.LocalTime;

/** Item da agenda diária: uma dose de medicamento ou uma rotina. */
public record AgendaItem(LocalTime horario, String tipo, String descricao, Boolean tomada) {
}
