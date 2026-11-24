package cl.duoc.incidencias;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@CrossOrigin
public class IncidenciaController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final IncidenciaRepository repo;

    public IncidenciaController(IncidenciaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Incidencia> listar(@RequestParam(required = false) Long siloId) {
        return siloId == null ? repo.findAll() : repo.findBySiloId(siloId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidencia> obtener(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Incidencia crear(@Valid @RequestBody Incidencia incidencia) {
        incidencia.setId(null);
        incidencia.setEstado("ABIERTA");
        incidencia.setFecha(LocalDateTime.now().format(FORMATO));
        return repo.save(incidencia);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incidencia> actualizar(@PathVariable Long id,
                                                 @Valid @RequestBody Incidencia datos) {
        return repo.findById(id).map(actual -> {
            actual.setSiloId(datos.getSiloId());
            actual.setDescripcion(datos.getDescripcion());
            if (datos.getEstado() != null) actual.setEstado(datos.getEstado());
            return ResponseEntity.ok(repo.save(actual));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<Incidencia> resolver(@PathVariable Long id) {
        return repo.findById(id).map(inc -> {
            inc.setEstado("RESUELTA");
            return ResponseEntity.ok(repo.save(inc));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}