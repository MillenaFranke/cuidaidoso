package br.com.fiap.cuidaidoso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record RotinaRequest(
        @NotBlank String descricao,
        @NotNull DayOfWeek diaSemana,
        @NotNull LocalTime horario) {
}
