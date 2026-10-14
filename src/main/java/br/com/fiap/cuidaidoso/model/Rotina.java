package br.com.fiap.cuidaidoso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.DayOfWeek;
import java.time.LocalTime;

/** Rotina semanal de saúde e bem-estar, como caminhada, fisioterapia ou consulta. */
@Entity
public class Rotina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idoso_id")
    private Idoso idoso;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;

    @Column(name = "horario_rotina", nullable = false)
    private LocalTime horario;

    protected Rotina() {
    }

    public Rotina(Idoso idoso, String descricao, DayOfWeek diaSemana, LocalTime horario) {
        this.idoso = idoso;
        this.descricao = descricao;
        this.diaSemana = diaSemana;
        this.horario = horario;
    }

    public Long getId() {
        return id;
    }

    public Idoso getIdoso() {
        return idoso;
    }

    public String getDescricao() {
        return descricao;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getHorario() {
        return horario;
    }
}
