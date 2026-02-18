package com.OrtegaAlvaro.ClinicaVeterinaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la transferencia de datos de Veterinario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String numeroColegiado;
    private String especialidad;
    private String email;
}
