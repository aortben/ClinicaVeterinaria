package com.OrtegaAlvaro.ClinicaVeterinaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la transferencia de datos de Cliente hacia/desde la API REST.
 * Aplana la relación con Mascotas para evitar recursión JSON.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String telefono;
    private String telefonoFormateado;
    private String direccion;
    private String email;
}
