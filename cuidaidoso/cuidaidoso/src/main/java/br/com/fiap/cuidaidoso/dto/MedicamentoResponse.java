package br.com.fiap.cuidaidoso.dto;

import br.com.fiap.cuidaidoso.model.Medicamento;
import java.time.LocalTime;
import java.util.List;

public record MedicamentoResponse(Long id, Long idosoId, String nome, String dosagem, List<LocalTime> horarios,
                                  boolean ativo) {

    public static MedicamentoResponse de(Medicamento m) {
        return new MedicamentoResponse(m.getId(), m.getIdoso().getId(), m.getNome(), m.getDosagem(),
                m.getHorarios().stream().sorted().toList(), m.isAtivo());
    }
}
