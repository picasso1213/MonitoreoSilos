package cl.duoc.incidencias;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findBySiloId(Long siloId);
}