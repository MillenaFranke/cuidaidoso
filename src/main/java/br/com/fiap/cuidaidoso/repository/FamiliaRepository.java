package br.com.fiap.cuidaidoso.repository;

import br.com.fiap.cuidaidoso.model.Familia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamiliaRepository extends JpaRepository<Familia, Long> {

    boolean existsByEmail(String email);
}
