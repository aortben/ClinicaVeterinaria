package com.OrtegaAlvaro.ClinicaVeterinaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad central del sistema que representa un evento médico o servicio
 * agendado.
 * Actúa como punto de unión entre el Paciente (Mascota), el Profesional
 * (Veterinario)
 * y los servicios económicos realizados (Tratamientos).
 */
@Entity
@Table(name = "cita_veterinaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaVeterinaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    @NotNull(message = "La fecha y hora son obligatorias")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo de la consulta es obligatorio")
    private String motivo;

    private String diagnostico;

    /**
     * Estado actual del flujo de la cita (Ej: Pendiente, Realizada, Cancelada).
     */
    private String estado;

    // --- RELACIONES ---

    /**
     * Mascota que recibe el servicio.
     * Relación N:1 (Muchas citas para una mascota).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    @NotNull(message = "Debes seleccionar una mascota")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Mascota mascota;

    /**
     * Profesional que atiende la cita.
     * Relación N:1 (Un veterinario atiende muchas citas).
     * Nullable: si el veterinario se da de baja, la cita permanece con veterinario
     * = NULL.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Veterinario veterinario;

    /**
     * Lista de servicios o tratamientos aplicados en esta cita.
     * Relación 1:N con cascada total (Si se borra la cita, se borran sus líneas de
     * tratamiento).
     */
    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Tratamiento> tratamientos = new ArrayList<>();

    // --- MÉTODOS DE LÓGICA DE NEGOCIO ---

    /**
     * Método helper para establecer la relación bidireccional
     * al añadir un tratamiento a la lista.
     * 
     * @param tratamiento El servicio a añadir.
     */
    public void addTratamiento(Tratamiento tratamiento) {
        tratamientos.add(tratamiento);
        tratamiento.setCita(this);
    }

    /**
     * Calcula el coste total de la cita sumando el precio de todos los tratamientos
     * asociados.
     * 
     * @return Suma total o 0.0 si no hay tratamientos.
     */
    public Double getCosteTotal() {
        if (tratamientos == null || tratamientos.isEmpty()) {
            return 0.0;
        }
        return tratamientos.stream()
                .mapToDouble(Tratamiento::getPrecio)
                .sum();
    }

    // --- MÉTODOS HELPER PARA LA VISTA (DTO-Like) ---
    // Estos métodos facilitan la visualización en Thymeleaf manejando posibles
    // nulos
    // si las entidades relacionadas (Mascota/Veterinario) han sido eliminadas.

    /**
     * Devuelve el nombre completo del veterinario de forma segura.
     * 
     * @return Nombre del veterinario o texto de aviso si es null.
     */
    public String getNombreVeterinarioStr() {
        if (this.veterinario == null) {
            return "No asignado (Baja)";
        }
        return this.veterinario.getNombre() + " " + this.veterinario.getApellidos();
    }

    /**
     * Devuelve el nombre de la mascota de forma segura.
     * 
     * @return Nombre de la mascota o texto de aviso si es null.
     */
    public String getNombreMascotaStr() {
        if (this.mascota == null) {
            return "Mascota eliminada";
        }
        return this.mascota.getNombre();
    }
}
