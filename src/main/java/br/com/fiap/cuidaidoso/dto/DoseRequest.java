package br.com.fiap.cuidaidoso.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record DoseRequest(
        @NotNull LocalDate data,
        @NotNull LocalTime horario,
        boolean tomada) {
}
