package br.com.fiap.cuidaidoso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record IdosoRequest(
        @NotBlank String nome,
        @NotNull @Past LocalDate dataNascimento,
        @Size(max = 500) String observacoes) {
}
