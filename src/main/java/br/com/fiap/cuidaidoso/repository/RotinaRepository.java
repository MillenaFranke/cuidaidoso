package br.com.fiap.cuidaidoso.repository;

import br.com.fiap.cuidaidoso.model.Rotina;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RotinaRepository extends JpaRepository<Rotina, Long> {

    List<Rotina> findByIdosoIdAndDiaSemanaOrderByHorario(Long idosoId, DayOfWeek diaSemana);
}
