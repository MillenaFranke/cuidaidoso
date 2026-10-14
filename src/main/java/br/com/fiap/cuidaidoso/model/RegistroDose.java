package br.com.fiap.cuidaidoso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;

/** Confirmação (ou não) de uma dose prevista para um dia e horário. */
@Entity
public class RegistroDose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;

    @Column(name = "data_dose", nullable = false)
    private LocalDate data;

    @Column(name = "horario_previsto", nullable = false)
    private LocalTime horario;

    @Column(nullable = false)
    private boolean tomada;

    protected RegistroDose() {
    }

    public RegistroDose(Medicamento medicamento, LocalDate data, LocalTime horario, boolean tomada) {
        this.medicamento = medicamento;
        this.data = data;
        this.horario = horario;
        this.tomada = tomada;
    }

    public void atualizar(boolean tomada) {
        this.tomada = tomada;
    }

    public Long getId() {
        return id;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public boolean isTomada() {
        return tomada;
    }
}
