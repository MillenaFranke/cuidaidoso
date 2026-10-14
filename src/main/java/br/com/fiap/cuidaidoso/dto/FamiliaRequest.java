package br.com.fiap.cuidaidoso.dto;

import br.com.fiap.cuidaidoso.model.Plano;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FamiliaRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotNull Plano plano) {
}
