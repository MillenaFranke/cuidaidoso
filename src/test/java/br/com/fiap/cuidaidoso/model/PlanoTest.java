package br.com.fiap.cuidaidoso.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PlanoTest {

    @Test
    void planoBasicoAceitaAteDoisIdosos() {
        assertTrue(Plano.BASICO.permiteNovoIdoso(0));
        assertTrue(Plano.BASICO.permiteNovoIdoso(1));
        assertFalse(Plano.BASICO.permiteNovoIdoso(2));
    }

    @Test
    void planoPremiumNaoTemLimitePratico() {
        assertTrue(Plano.PREMIUM.permiteNovoIdoso(50));
    }

    @Test
    void idadeEhCalculadaAPartirDaDataDeNascimento() {
        Familia familia = new Familia("Silva", "silva@exemplo.com", Plano.BASICO);
        Idoso idoso = new Idoso(familia, "Dona Maria", LocalDate.now().minusYears(80).minusDays(1), null);
        assertEquals(80, idoso.calcularIdade());
    }
}
