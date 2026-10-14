package br.com.fiap.cuidaidoso.dto;

import br.com.fiap.cuidaidoso.model.Idoso;
import java.time.LocalDate;

public record IdosoResponse(Long id, Long familiaId, String nome, LocalDate dataNascimento, int idade,
                            String observacoes) {

    public static IdosoResponse de(Idoso i) {
        return new IdosoResponse(i.getId(), i.getFamilia().getId(), i.getNome(), i.getDataNascimento(),
                i.calcularIdade(), i.getObservacoes());
    }
}
