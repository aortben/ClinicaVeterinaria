package com.OrtegaAlvaro.ClinicaVeterinaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para la transferencia de datos de CitaVeterinaria.
 * Incluye referencias aplanadas a Mascota y Veterinario, así como
 * la lista de tratamientos y el coste total calculado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaVeterinariaDTO {

    private Long id;
    private LocalDateTime fechaHora;
    private String motivo;
    private String diagnostico;
    private String estado;
    private Long mascotaId;
    private String mascotaNombre;
    private Long veterinarioId;
    private String veterinarioNombre;
    private Double costeTotal;
    private List<TratamientoDTO> tratamientos;
}
