package br.com.fiap.cuidaidoso.dto;

import java.time.LocalDate;
import java.util.List;

public record AgendaResponse(Long idosoId, String nome, LocalDate data, List<AgendaItem> itens) {
}
