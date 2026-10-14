package br.com.fiap.cuidaidoso.dto;

import br.com.fiap.cuidaidoso.model.Familia;
import br.com.fiap.cuidaidoso.model.Plano;

public record FamiliaResponse(Long id, String nome, String email, Plano plano, int limiteIdosos) {

    public static FamiliaResponse de(Familia f) {
        int limite = f.getPlano().getLimiteIdosos();
        return new FamiliaResponse(f.getId(), f.getNome(), f.getEmail(), f.getPlano(),
                limite == Integer.MAX_VALUE ? -1 : limite);
    }
}
