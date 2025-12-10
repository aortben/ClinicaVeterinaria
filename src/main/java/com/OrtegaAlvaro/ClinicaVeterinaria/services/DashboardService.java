package com.OrtegaAlvaro.ClinicaVeterinaria.services;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.CitaVeterinaria;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.CitaVeterinariaRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.ClienteRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.MascotaRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio especializado en la agregación de datos para el Panel de Control (Dashboard).
 * Su función es consolidar información de múltiples repositorios para ofrecer
 * una vista resumen del estado actual de la clínica.
 */
@Service
public class DashboardService {

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private MascotaRepository mascotaRepo;

    @Autowired
    private VeterinarioRepository veterinarioRepo;

    @Autowired
    private CitaVeterinariaRepository citaRepo;

    /**
     * Recopila las métricas globales del sistema (KPIs).
     * Calcula los totales de las entidades principales para alimentar las tarjetas
     * informativas de la página de inicio.
     *
     * @return Mapa con claves (String) y valores (Long) listos para la vista.
     */
    public Map<String, Long> obtenerEstadisticasGenerales() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("totalClientes", clienteRepo.count());
        stats.put("totalMascotas", mascotaRepo.count());
        stats.put("totalVeterinarios", veterinarioRepo.count());
        stats.put("totalCitas", citaRepo.count());

        return stats;
    }

    /**
     * Recupera el historial operativo más reciente.
     * Obtiene las últimas 5 citas registradas (independientemente de su estado)
     * para mostrar la actividad reciente en el sistema.
     *
     * @return Lista limitada de citas ordenadas cronológicamente (DESC).
     */
    public List<CitaVeterinaria> obtenerUltimasCitas() {
        return citaRepo.findTop5ByOrderByFechaHoraDesc();
    }
}
