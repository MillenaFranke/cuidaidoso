package br.com.fiap.cuidaidoso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalTime;
import java.util.List;

public record MedicamentoRequest(
        @NotBlank String nome,
        @NotBlank String dosagem,
        @NotEmpty List<LocalTime> horarios) {
}
