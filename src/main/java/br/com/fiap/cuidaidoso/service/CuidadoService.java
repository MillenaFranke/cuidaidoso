package br.com.fiap.cuidaidoso.service;

import br.com.fiap.cuidaidoso.dto.AgendaItem;
import br.com.fiap.cuidaidoso.dto.AgendaResponse;
import br.com.fiap.cuidaidoso.dto.DoseRequest;
import br.com.fiap.cuidaidoso.dto.FamiliaRequest;
import br.com.fiap.cuidaidoso.dto.IdosoRequest;
import br.com.fiap.cuidaidoso.dto.MedicamentoRequest;
import br.com.fiap.cuidaidoso.dto.RotinaRequest;
import br.com.fiap.cuidaidoso.exception.RecursoNaoEncontradoException;
import br.com.fiap.cuidaidoso.exception.RegraDeNegocioException;
import br.com.fiap.cuidaidoso.model.Familia;
import br.com.fiap.cuidaidoso.model.Idoso;
import br.com.fiap.cuidaidoso.model.Medicamento;
import br.com.fiap.cuidaidoso.model.RegistroDose;
import br.com.fiap.cuidaidoso.model.Rotina;
import br.com.fiap.cuidaidoso.repository.FamiliaRepository;
import br.com.fiap.cuidaidoso.repository.IdosoRepository;
import br.com.fiap.cuidaidoso.repository.MedicamentoRepository;
import br.com.fiap.cuidaidoso.repository.RegistroDoseRepository;
import br.com.fiap.cuidaidoso.repository.RotinaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuidadoService {

    private final FamiliaRepository familias;
    private final IdosoRepository idosos;
    private final MedicamentoRepository medicamentos;
    private final RegistroDoseRepository doses;
    private final RotinaRepository rotinas;

    public CuidadoService(FamiliaRepository familias, IdosoRepository idosos, MedicamentoRepository medicamentos,
                          RegistroDoseRepository doses, RotinaRepository rotinas) {
        this.familias = familias;
        this.idosos = idosos;
        this.medicamentos = medicamentos;
        this.doses = doses;
        this.rotinas = rotinas;
    }

    public Familia criarFamilia(FamiliaRequest req) {
        if (familias.existsByEmail(req.email())) {
            throw new RegraDeNegocioException("Já existe uma família cadastrada com este e-mail");
        }
        return familias.save(new Familia(req.nome(), req.email(), req.plano()));
    }

    @Transactional(readOnly = true)
    public Familia buscarFamilia(Long id) {
        return familias.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Família não encontrada: " + id));
    }

    /** Cadastra o idoso respeitando o limite de idosos do plano da família. */
    public Idoso cadastrarIdoso(Long familiaId, IdosoRequest req) {
        Familia familia = buscarFamilia(familiaId);
        long cadastrados = idosos.countByFamiliaId(familiaId);
        if (!familia.getPlano().permiteNovoIdoso(cadastrados)) {
            throw new RegraDeNegocioException("O plano " + familia.getPlano()
                    + " permite no máximo " + familia.getPlano().getLimiteIdosos() + " idosos");
        }
        return idosos.save(new Idoso(familia, req.nome(), req.dataNascimento(), req.observacoes()));
    }

    @Transactional(readOnly = true)
    public List<Idoso> listarIdosos(Long familiaId) {
        buscarFamilia(familiaId);
        return idosos.findByFamiliaId(familiaId);
    }

    @Transactional(readOnly = true)
    public Idoso buscarIdoso(Long id) {
        return idosos.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso não encontrado: " + id));
    }

    public Medicamento cadastrarMedicamento(Long idosoId, MedicamentoRequest req) {
        Idoso idoso = buscarIdoso(idosoId);
        return medicamentos.save(new Medicamento(idoso, req.nome(), req.dosagem(), req.horarios()));
    }

    @Transactional(readOnly = true)
    public List<Medicamento> listarMedicamentos(Long idosoId) {
        buscarIdoso(idosoId);
        return medicamentos.findByIdosoIdAndAtivoTrue(idosoId);
    }

    /** Registra (ou atualiza) a confirmação de uma dose prevista para o dia e horário informados. */
    public RegistroDose registrarDose(Long medicamentoId, DoseRequest req) {
        Medicamento medicamento = medicamentos.findById(medicamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Medicamento não encontrado: " + medicamentoId));
        if (!medicamento.getHorarios().contains(req.horario())) {
            throw new RegraDeNegocioException("O horário " + req.horario() + " não faz parte da prescrição");
        }
        Optional<RegistroDose> existente = doses
                .findByMedicamentoIdAndDataAndHorario(medicamentoId, req.data(), req.horario());
        if (existente.isPresent()) {
            existente.get().atualizar(req.tomada());
            return existente.get();
        }
        return doses.save(new RegistroDose(medicamento, req.data(), req.horario(), req.tomada()));
    }

    public Rotina cadastrarRotina(Long idosoId, RotinaRequest req) {
        Idoso idoso = buscarIdoso(idosoId);
        return rotinas.save(new Rotina(idoso, req.descricao(), req.diaSemana(), req.horario()));
    }

    /** Monta a agenda do dia: doses previstas (com status) e rotinas do dia da semana, em ordem de horário. */
    @Transactional(readOnly = true)
    public AgendaResponse agenda(Long idosoId, LocalDate data) {
        Idoso idoso = buscarIdoso(idosoId);
        List<Medicamento> ativos = medicamentos.findByIdosoIdAndAtivoTrue(idosoId);
        List<Long> ids = ativos.stream().map(Medicamento::getId).toList();

        Set<String> tomadas = ids.isEmpty() ? Set.of() : doses.findByMedicamentoIdInAndData(ids, data).stream()
                .filter(RegistroDose::isTomada)
                .map(d -> d.getMedicamento().getId() + "@" + d.getHorario())
                .collect(Collectors.toSet());

        List<AgendaItem> itens = new ArrayList<>();
        for (Medicamento m : ativos) {
            for (LocalTime h : m.getHorarios()) {
                boolean tomada = tomadas.contains(m.getId() + "@" + h);
                itens.add(new AgendaItem(h, "MEDICAMENTO", m.getNome() + " " + m.getDosagem(), tomada));
            }
        }
        for (Rotina r : rotinas.findByIdosoIdAndDiaSemanaOrderByHorario(idosoId, data.getDayOfWeek())) {
            itens.add(new AgendaItem(r.getHorario(), "ROTINA", r.getDescricao(), null));
        }
        itens.sort(Comparator.comparing(AgendaItem::horario));
        return new AgendaResponse(idoso.getId(), idoso.getNome(), data, itens);
    }
}
