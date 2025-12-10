package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.CitaVeterinaria;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Tratamiento;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.CitaVeterinariaService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.TratamientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador encargado de la gestión de Tratamientos y Servicios clínicos.
 * Esta entidad actúa como detalle económico o línea de servicio asociada a una Cita Veterinaria.
 * Gestiona el flujo de navegación contextual, permitiendo operaciones desde el listado general
 * o desde la ficha de edición de una cita (Maestro-Detalle).
 */
@Controller
@RequestMapping("/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private CitaVeterinariaService citaService;

    /**
     * Muestra el historial global de todos los tratamientos realizados en la clínica.
     * Útil para auditoría y revisión de facturación general.
     */
    @GetMapping
    public String listarGlobal(Model model) {
        // Reutiliza la vista 'entities-html/Tratamiento' adaptada para mostrarse sin contexto de cita
        model.addAttribute("tratamientos", tratamientoService.findAll());
        return "entities-html/Tratamiento";
    }

    /**
     * Muestra el listado de tratamientos filtrados para una cita específica.
     * @param citaId Identificador de la cita padre.
     */
    @GetMapping("/cita/{citaId}")
    public String listarPorCita(@PathVariable Long citaId, Model model, RedirectAttributes redirect) {
        Optional<CitaVeterinaria> citaOpt = citaService.findById(citaId);

        if (citaOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "No se puede cargar el detalle: La cita asociada no existe.");
            return "redirect:/citas";
        }

        // Se pasa la cita al modelo para mostrar la cabecera contextual en la vista
        model.addAttribute("cita", citaOpt.get());
        model.addAttribute("tratamientos", tratamientoService.findByCitaId(citaId));
        return "entities-html/Tratamiento";
    }

    /**
     * Prepara el formulario para añadir un nuevo tratamiento a una cita existente.
     * @param citaId ID de la cita a la que se vinculará el servicio.
     */
    @GetMapping("/nuevo/{citaId}")
    public String formularioCrear(@PathVariable Long citaId, Model model, RedirectAttributes redirect) {
        Optional<CitaVeterinaria> citaOpt = citaService.findById(citaId);

        if (citaOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "Cita no encontrada.");
            return "redirect:/citas";
        }

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setCita(citaOpt.get()); // Vinculación automática (Pre-asignación)

        model.addAttribute("tratamiento", tratamiento);
        model.addAttribute("citaId", citaId);
        model.addAttribute("titulo", "Añadir Tratamiento / Servicio");

        return "forms-html/Tratamiento-form";
    }

    /**
     * Prepara el formulario para la edición de un tratamiento existente.
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return tratamientoService.findById(id).map(trat -> {
            model.addAttribute("tratamiento", trat);
            model.addAttribute("citaId", trat.getCita().getId());
            model.addAttribute("titulo", "Editar Tratamiento");
            return "forms-html/Tratamiento-form";
        }).orElseGet(() -> {
            redirect.addFlashAttribute("error", "El tratamiento solicitado no existe.");
            return "redirect:/citas";
        });
    }

    /**
     * Procesa la persistencia de un tratamiento.
     * Soporta redirección dinámica basada en el origen de la petición para mejorar la UX.
     *
     * @param origen Parámetro opcional que indica desde dónde se realizó la acción
     * ('lista' o 'editar_cita') para retornar a la vista correcta.
     */
    @PostMapping("/guardar")
    public String guardarTratamiento(
            @Valid @ModelAttribute Tratamiento tratamiento,
            BindingResult result,
            Model model,
            @RequestParam(required = false, defaultValue = "lista") String origen
    ) {
        Long citaId = tratamiento.getCita().getId();

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar Tratamiento");
            model.addAttribute("citaId", citaId);
            return "forms-html/Tratamiento-form";
        }

        tratamientoService.save(tratamiento);

        // Lógica de navegación: Si el usuario venía de editar la cita completa, volvemos allí.
        if ("editar_cita".equals(origen)) {
            return "redirect:/citas/editar/" + citaId;
        }

        // Por defecto, volvemos al listado de tratamientos de esa cita
        return "redirect:/tratamientos/cita/" + citaId;
    }

    /**
     * Elimina un tratamiento del sistema.
     * Mantiene la lógica de redirección dinámica según el origen.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarTratamiento(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "lista") String origen,
            RedirectAttributes redirect
    ) {
        Optional<Tratamiento> tratOpt = tratamientoService.findById(id);

        if (tratOpt.isPresent()) {
            Long citaId = tratOpt.get().getCita().getId();
            tratamientoService.deleteById(id);
            redirect.addFlashAttribute("success", "Tratamiento eliminado correctamente.");

            // Retorno al formulario maestro de la cita si la petición vino de allí
            if ("editar_cita".equals(origen)) {
                return "redirect:/citas/editar/" + citaId;
            }
            return "redirect:/tratamientos/cita/" + citaId;
        }

        return "redirect:/citas";
    }
}