package br.com.fiap.cuidaidoso.repository;

import br.com.fiap.cuidaidoso.model.Medicamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    List<Medicamento> findByIdosoIdAndAtivoTrue(Long idosoId);
}
