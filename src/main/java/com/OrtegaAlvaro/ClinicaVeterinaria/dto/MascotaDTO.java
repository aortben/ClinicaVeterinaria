package com.OrtegaAlvaro.ClinicaVeterinaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para la transferencia de datos de Mascota.
 * Incluye clienteId y clienteNombre para representar la relación sin anidar el
 * objeto completo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDTO {

    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private Double peso;
    private Long clienteId;
    private String clienteNombre;
}
