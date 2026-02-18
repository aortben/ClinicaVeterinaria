package com.OrtegaAlvaro.ClinicaVeterinaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la transferencia de datos de Tratamiento.
 * Incluye citaId para mantener la referencia sin anidar el objeto
 * CitaVeterinaria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TratamientoDTO {

    private Long id;
    private String descripcion;
    private String medicamento;
    private Double precio;
    private String observaciones;
    private Long citaId;
}
