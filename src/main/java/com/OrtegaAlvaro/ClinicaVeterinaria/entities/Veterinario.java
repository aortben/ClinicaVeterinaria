package com.OrtegaAlvaro.ClinicaVeterinaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un miembro del equipo médico (Veterinario).
 * Gestiona la información profesional, número de colegiado y especialidad,
 * actuando como recurso asignable en las Citas Veterinarias.
 */
@Entity
@Table(name = "veterinario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @Column(name = "numero_colegiado", unique = true, nullable = false)
    @NotBlank(message = "El número de colegiado es obligatorio")
    private String numeroColegiado;

    private String especialidad;

    @Email(message = "El formato del correo electrónico no es válido")
    @NotBlank(message = "El email de contacto es obligatorio")
    private String email;

    // --- RELACIONES ---

    /**
     * Historial de citas atendidas por este profesional.
     * * NOTA DE ARQUITECTURA:
     * No se aplica 'CascadeType.REMOVE' ni 'orphanRemoval = true' intencionadamente.
     * Si un veterinario es eliminado del sistema, sus citas pasadas deben
     * PERMANECER en la base de datos como registro histórico (con veterinario_id = NULL).
     */
    @OneToMany(mappedBy = "veterinario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CitaVeterinaria> citas = new ArrayList<>();
}