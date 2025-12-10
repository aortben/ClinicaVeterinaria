package com.OrtegaAlvaro.ClinicaVeterinaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa al Paciente (Animal) dentro del sistema clínico.
 * Almacena los datos fisiológicos básicos y mantiene el historial médico (Citas)
 * así como la vinculación con su propietario (Cliente).
 */
@Entity
@Table(name = "mascota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    private String raza;

    @Column(name = "fecha_nacimiento")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Positive(message = "El peso debe ser un valor positivo")
    private Double peso;

    // --- RELACIONES ---

    /**
     * Propietario legal de la mascota.
     * Relación N:1 (Un cliente puede tener múltiples mascotas).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "Debe asignar un propietario a la mascota")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    /**
     * Historial médico de la mascota.
     * Relación 1:N. Si se elimina la mascota, se elimina su historial completo (CascadeType.ALL).
     */
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CitaVeterinaria> citas = new ArrayList<>();
}