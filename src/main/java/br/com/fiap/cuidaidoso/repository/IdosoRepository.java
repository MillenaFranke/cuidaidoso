package br.com.fiap.cuidaidoso.repository;

import br.com.fiap.cuidaidoso.model.Idoso;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdosoRepository extends JpaRepository<Idoso, Long> {

    long countByFamiliaId(Long familiaId);

    List<Idoso> findByFamiliaId(Long familiaId);
}
