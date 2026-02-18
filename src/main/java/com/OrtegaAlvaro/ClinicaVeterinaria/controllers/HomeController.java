package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.CitaVeterinariaDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.dto.DashboardDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.dto.TratamientoDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.CitaVeterinaria;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Tratamiento;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el Panel de Control (Dashboard).
 * Proporciona métricas agregadas y las últimas citas del sistema.
 */
@RestController
@RequestMapping("/api/dashboard")
public class HomeController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Obtiene las estadísticas generales y las últimas citas.
     * GET /api/dashboard
     */
    @GetMapping
    public ResponseEntity<DashboardDTO> obtenerDashboard() {
        Map<String, Long> stats = dashboardService.obtenerEstadisticasGenerales();
        List<CitaVeterinaria> ultimasCitas = dashboardService.obtenerUltimasCitas();

        List<CitaVeterinariaDTO> citaDTOs = ultimasCitas.stream()
                .map(this::toDTO)
                .toList();

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setStats(stats);
        dashboard.setUltimasCitas(citaDTOs);

        return ResponseEntity.ok(dashboard);
    }

    // --- Conversiones Entity → DTO ---

    private CitaVeterinariaDTO toDTO(CitaVeterinaria c) {
        CitaVeterinariaDTO dto = new CitaVeterinariaDTO();
        dto.setId(c.getId());
        dto.setFechaHora(c.getFechaHora());
        dto.setMotivo(c.getMotivo());
        dto.setDiagnostico(c.getDiagnostico());
        dto.setEstado(c.getEstado());
        dto.setCosteTotal(c.getCosteTotal());

        if (c.getMascota() != null) {
            dto.setMascotaId(c.getMascota().getId());
            dto.setMascotaNombre(c.getNombreMascotaStr());
        }
        if (c.getVeterinario() != null) {
            dto.setVeterinarioId(c.getVeterinario().getId());
            dto.setVeterinarioNombre(c.getNombreVeterinarioStr());
        }

        if (c.getTratamientos() != null) {
            dto.setTratamientos(c.getTratamientos().stream()
                    .map(this::tratamientoToDTO)
                    .toList());
        } else {
            dto.setTratamientos(Collections.emptyList());
        }

        return dto;
    }

    private TratamientoDTO tratamientoToDTO(Tratamiento t) {
        TratamientoDTO dto = new TratamientoDTO();
        dto.setId(t.getId());
        dto.setDescripcion(t.getDescripcion());
        dto.setMedicamento(t.getMedicamento());
        dto.setPrecio(t.getPrecio());
        dto.setObservaciones(t.getObservaciones());
        dto.setCitaId(t.getCita() != null ? t.getCita().getId() : null);
        return dto;
    }
}
