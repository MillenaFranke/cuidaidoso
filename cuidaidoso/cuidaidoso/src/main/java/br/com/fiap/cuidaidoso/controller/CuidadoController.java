package br.com.fiap.cuidaidoso.controller;

import br.com.fiap.cuidaidoso.dto.AgendaResponse;
import br.com.fiap.cuidaidoso.dto.DoseRequest;
import br.com.fiap.cuidaidoso.dto.FamiliaRequest;
import br.com.fiap.cuidaidoso.dto.FamiliaResponse;
import br.com.fiap.cuidaidoso.dto.IdosoRequest;
import br.com.fiap.cuidaidoso.dto.IdosoResponse;
import br.com.fiap.cuidaidoso.dto.MedicamentoRequest;
import br.com.fiap.cuidaidoso.dto.MedicamentoResponse;
import br.com.fiap.cuidaidoso.dto.RotinaRequest;
import br.com.fiap.cuidaidoso.dto.RotinaResponse;
import br.com.fiap.cuidaidoso.service.CuidadoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CuidadoController {

    private final CuidadoService service;

    public CuidadoController(CuidadoService service) {
        this.service = service;
    }

    @PostMapping("/familias")
    @ResponseStatus(HttpStatus.CREATED)
    public FamiliaResponse criarFamilia(@Valid @RequestBody FamiliaRequest req) {
        return FamiliaResponse.de(service.criarFamilia(req));
    }

    @GetMapping("/familias/{id}")
    public FamiliaResponse buscarFamilia(@PathVariable Long id) {
        return FamiliaResponse.de(service.buscarFamilia(id));
    }

    @PostMapping("/familias/{familiaId}/idosos")
    @ResponseStatus(HttpStatus.CREATED)
    public IdosoResponse cadastrarIdoso(@PathVariable Long familiaId, @Valid @RequestBody IdosoRequest req) {
        return IdosoResponse.de(service.cadastrarIdoso(familiaId, req));
    }

    @GetMapping("/familias/{familiaId}/idosos")
    public List<IdosoResponse> listarIdosos(@PathVariable Long familiaId) {
        return service.listarIdosos(familiaId).stream().map(IdosoResponse::de).toList();
    }

    @PostMapping("/idosos/{idosoId}/medicamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicamentoResponse cadastrarMedicamento(@PathVariable Long idosoId,
                                                    @Valid @RequestBody MedicamentoRequest req) {
        return MedicamentoResponse.de(service.cadastrarMedicamento(idosoId, req));
    }

    @GetMapping("/idosos/{idosoId}/medicamentos")
    public List<MedicamentoResponse> listarMedicamentos(@PathVariable Long idosoId) {
        return service.listarMedicamentos(idosoId).stream().map(MedicamentoResponse::de).toList();
    }

    @PutMapping("/medicamentos/{medicamentoId}/doses")
    public void registrarDose(@PathVariable Long medicamentoId, @Valid @RequestBody DoseRequest req) {
        service.registrarDose(medicamentoId, req);
    }

    @PostMapping("/idosos/{idosoId}/rotinas")
    @ResponseStatus(HttpStatus.CREATED)
    public RotinaResponse cadastrarRotina(@PathVariable Long idosoId, @Valid @RequestBody RotinaRequest req) {
        return RotinaResponse.de(service.cadastrarRotina(idosoId, req));
    }

    @GetMapping("/idosos/{idosoId}/agenda")
    public AgendaResponse agenda(@PathVariable Long idosoId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return service.agenda(idosoId, data);
    }
}
