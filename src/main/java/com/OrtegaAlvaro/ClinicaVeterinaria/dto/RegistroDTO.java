package com.OrtegaAlvaro.ClinicaVeterinaria.dto;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistroDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email no válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    // Campos opcionales dependiendo del rol
    private String dni; // Para CLIENTE
    private String telefono; // Para CLIENTE
    private String direccion; // Para CLIENTE

    private String numeroColegiado; // Para VETERINARIO
    private String especialidad; // Para VETERINARIO
}
