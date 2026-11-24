package cl.duoc.silos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Silo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La granja es obligatoria")
    private String granja;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private int capacidadKg;

    @Min(value = 0, message = "El nivel no puede ser negativo")
    private int nivelKg;

    @NotBlank(message = "El tipo de alimento es obligatorio")
    private String tipoAlimento;

    @Min(value = 0, message = "El consumo diario no puede ser negativo")
    private int consumoDiarioKg;

    public Silo() {
    }

    public Silo(String granja, String nombre, int capacidadKg, int nivelKg,
                String tipoAlimento, int consumoDiarioKg) {
        this.granja = granja;
        this.nombre = nombre;
        this.capacidadKg = capacidadKg;
        this.nivelKg = nivelKg;
        this.tipoAlimento = tipoAlimento;
        this.consumoDiarioKg = consumoDiarioKg;
    }

    public int porcentaje() {
        return capacidadKg == 0 ? 0 : (nivelKg * 100) / capacidadKg;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGranja() { return granja; }
    public void setGranja(String granja) { this.granja = granja; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCapacidadKg() { return capacidadKg; }
    public void setCapacidadKg(int capacidadKg) { this.capacidadKg = capacidadKg; }
    public int getNivelKg() { return nivelKg; }
    public void setNivelKg(int nivelKg) { this.nivelKg = nivelKg; }
    public String getTipoAlimento() { return tipoAlimento; }
    public void setTipoAlimento(String tipoAlimento) { this.tipoAlimento = tipoAlimento; }
    public int getConsumoDiarioKg() { return consumoDiarioKg; }
    public void setConsumoDiarioKg(int consumoDiarioKg) { this.consumoDiarioKg = consumoDiarioKg; }
}