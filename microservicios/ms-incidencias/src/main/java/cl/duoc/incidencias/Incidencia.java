package cl.duoc.incidencias;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El silo es obligatorio")
    private Long siloId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 255, message = "La descripción debe tener entre 5 y 255 caracteres")
    private String descripcion;

    private String estado = "ABIERTA";

    private String fecha;

    public Incidencia() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSiloId() { return siloId; }
    public void setSiloId(Long siloId) { this.siloId = siloId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}