package br.com.fiap.cuidaidoso.model;

/**
 * Planos de assinatura mensal da família. O limite de idosos por plano é uma regra
 * definida neste projeto e pode ser ajustada aqui.
 */
public enum Plano {
    BASICO(2),
    PREMIUM(Integer.MAX_VALUE);

    private final int limiteIdosos;

    Plano(int limiteIdosos) {
        this.limiteIdosos = limiteIdosos;
    }

    public int getLimiteIdosos() {
        return limiteIdosos;
    }

    /** Indica se o plano aceita mais um idoso, dado quantos já estão cadastrados. */
    public boolean permiteNovoIdoso(long idososCadastrados) {
        return idososCadastrados < limiteIdosos;
    }
}
