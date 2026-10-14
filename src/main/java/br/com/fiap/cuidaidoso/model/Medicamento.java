package br.com.fiap.cuidaidoso.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idoso_id")
    private Idoso idoso;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String dosagem;

    /** Horários diários em que o medicamento deve ser tomado. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medicamento_horarios", joinColumns = @JoinColumn(name = "medicamento_id"))
    @Column(name = "horario_dose")
    private List<LocalTime> horarios = new ArrayList<>();

    @Column(nullable = false)
    private boolean ativo = true;

    protected Medicamento() {
    }

    public Medicamento(Idoso idoso, String nome, String dosagem, List<LocalTime> horarios) {
        this.idoso = idoso;
        this.nome = nome;
        this.dosagem = dosagem;
        this.horarios = new ArrayList<>(horarios);
    }

    public Long getId() {
        return id;
    }

    public Idoso getIdoso() {
        return idoso;
    }

    public String getNome() {
        return nome;
    }

    public String getDosagem() {
        return dosagem;
    }

    public List<LocalTime> getHorarios() {
        return horarios;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
