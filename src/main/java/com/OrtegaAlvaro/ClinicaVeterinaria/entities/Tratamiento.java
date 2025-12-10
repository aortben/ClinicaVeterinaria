package com.OrtegaAlvaro.ClinicaVeterinaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

/**
 * Entidad que representa un servicio clínico, procedimiento médico o medicamento suministrado.
 * Actúa como el detalle económico ("línea de factura") asociado a una Cita Veterinaria específica.
 */
@Entity
@Table(name = "tratamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La descripción del servicio es obligatoria")
    @Column(nullable = false)
    private String descripcion;

    /**
     * Nombre del fármaco suministrado, si aplica (Opcional).
     */
    private String medicamento;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double precio;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // --- RELACIONES ---

    /**
     * Cita a la que pertenece este tratamiento.
     * Relación N:1 obligatoria. Un tratamiento no puede existir sin una cita padre.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false)
    @NotNull(message = "El tratamiento debe estar vinculado a una cita")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CitaVeterinaria cita;
}