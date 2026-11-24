package cl.duoc.silos;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/silos")
@CrossOrigin
public class SiloController {

    private final SiloRepository repo;

    public SiloController(SiloRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Silo> listar() {
        return repo.findAll();
    }

    @GetMapping("/alertas")
    public List<Silo> alertas() {
        return repo.findAll().stream()
                .filter(s -> s.porcentaje() <= 30)
                .sorted((a, b) -> Integer.compare(a.porcentaje(), b.porcentaje()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Silo> obtener(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Silo crear(@Valid @RequestBody Silo silo) {
        silo.setId(null);
        return repo.save(silo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Silo> actualizar(@PathVariable Long id, @Valid @RequestBody Silo silo) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        silo.setId(id);
        return ResponseEntity.ok(repo.save(silo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}