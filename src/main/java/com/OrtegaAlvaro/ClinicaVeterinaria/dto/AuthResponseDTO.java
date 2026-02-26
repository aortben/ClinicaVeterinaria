package com.OrtegaAlvaro.ClinicaVeterinaria.dto;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String email;
    private Rol rol;
}
