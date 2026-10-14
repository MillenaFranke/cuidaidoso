package br.com.fiap.cuidaidoso.repository;

import br.com.fiap.cuidaidoso.model.RegistroDose;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroDoseRepository extends JpaRepository<RegistroDose, Long> {

    Optional<RegistroDose> findByMedicamentoIdAndDataAndHorario(Long medicamentoId, LocalDate data, LocalTime horario);

    List<RegistroDose> findByMedicamentoIdInAndData(List<Long> medicamentoIds, LocalDate data);
}
