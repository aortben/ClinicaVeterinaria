package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador principal de la aplicación.
 * Gestiona la vista de inicio (Dashboard), manejando la obtención de métricas
 * y resúmenes de actividad para el panel de control del usuario.
 */
@Controller
public class HomeController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Procesa la petición a la raíz del sitio web ("/").
     * Recopila estadísticas generales (contadores de entidades) y el historial reciente
     * de citas para proporcionar una visión global del estado de la clínica.
     *
     * @param model Objeto contenedor para transferir los datos (stats, ultimasCitas) a la vista.
     * @return La plantilla 'index' renderizada.
     */
    @GetMapping("/")
    public String mostrarDashboard(Model model) {
        // Carga de KPIs (Indicadores Clave de Rendimiento) para las tarjetas superiores
        model.addAttribute("stats", dashboardService.obtenerEstadisticasGenerales());

        // Carga de listado resumen operativo para la tabla central
        model.addAttribute("ultimasCitas", dashboardService.obtenerUltimasCitas());

        return "index";
    }
}
