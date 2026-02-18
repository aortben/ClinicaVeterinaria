package com.OrtegaAlvaro.ClinicaVeterinaria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO para el endpoint de Dashboard.
 * Agrupa las estadísticas generales y las últimas citas del sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private Map<String, Long> stats;
    private List<CitaVeterinariaDTO> ultimasCitas;
}
