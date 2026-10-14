package br.com.fiap.cuidaidoso.dto;

import br.com.fiap.cuidaidoso.model.Rotina;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record RotinaResponse(Long id, String descricao, DayOfWeek diaSemana, LocalTime horario) {

    public static RotinaResponse de(Rotina r) {
        return new RotinaResponse(r.getId(), r.getDescricao(), r.getDiaSemana(), r.getHorario());
    }
}
