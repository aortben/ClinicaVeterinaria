package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.CitaVeterinaria;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Tratamiento;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.CitaVeterinariaService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.MascotaService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Controlador principal para la gestión de Citas Veterinarias.
 * Gestiona el flujo de trabajo completo: agenda, asignación de recursos (Mascota/Veterinario)
 * y persistencia de datos transaccionales.
 */
@Controller
@RequestMapping("/citas")
public class CitaVeterinariaController {

    @Autowired
    private CitaVeterinariaService citaService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private VeterinarioService veterinarioService;

    /**
     * Recupera y muestra el listado global de citas registradas en el sistema.
     * @param model Modelo para pasar la lista a la vista.
     * @return La vista de listado de entidades.
     */
    @GetMapping
    public String listarCitas(Model model) {
        model.addAttribute("citas", citaService.findAll());
        return "entities-html/CitaVeterinaria";
    }

    /**
     * Muestra la vista de detalle extendido de una cita específica.
     * Incluye información cruzada de Cliente, Mascota, Veterinario y desglose de Tratamientos.
     * @param id Identificador de la cita.
     * @return Vista de detalle o redirección si no existe.
     */
    @GetMapping("/detalle/{id}")
    public String verDetalleCita(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<CitaVeterinaria> citaOpt = citaService.findById(id);

        if (citaOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "La cita solicitada no existe en la base de datos.");
            return "redirect:/citas";
        }

        model.addAttribute("cita", citaOpt.get());
        model.addAttribute("titulo", "Detalle de la Cita");
        return "details-html/CitaVeterinaria-details";
    }

    /**
     * Prepara el formulario para el registro de una nueva cita.
     * Inicializa las listas necesarias para los selectores de Mascota y Veterinario.
     */
    @GetMapping("/nueva")
    public String formularioCrear(Model model) {
        CitaVeterinaria cita = new CitaVeterinaria();
        // Inicializamos la lista para evitar errores en la vista si se intenta iterar
        cita.setTratamientos(new ArrayList<>());

        model.addAttribute("cita", cita);
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("veterinarios", veterinarioService.findAll());
        model.addAttribute("titulo", "Nueva Cita");

        return "forms-html/CitaVeterinaria-form";
    }

    /**
     * Prepara el formulario para la edición de una cita existente.
     * @param id Identificador de la cita a editar.
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<CitaVeterinaria> citaOpt = citaService.findById(id);

        if (citaOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "No se puede editar: Cita no encontrada.");
            return "redirect:/citas";
        }

        model.addAttribute("cita", citaOpt.get());
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("veterinarios", veterinarioService.findAll());
        model.addAttribute("titulo", "Editar Cita");

        return "forms-html/CitaVeterinaria-form";
    }

    /**
     * Procesa la persistencia (Guardado o Actualización) de la cita.
     * Implementa lógica específica para preservar la integridad de los tratamientos
     * al actualizar una cita existente, evitando la pérdida de datos relacionales.
     */
    @PostMapping("/guardar")
    public String guardarCita(
            @Valid @ModelAttribute("cita") CitaVeterinaria citaForm,
            BindingResult result,
            Model model,
            RedirectAttributes redirect
    ) {
        // 1. Validación de formulario
        if (result.hasErrors()) {
            model.addAttribute("mascotas", mascotaService.findAll());
            model.addAttribute("veterinarios", veterinarioService.findAll());
            model.addAttribute("titulo", citaForm.getId() == null ? "Nueva Cita" : "Editar Cita");
            return "forms-html/CitaVeterinaria-form";
        }

        CitaVeterinaria citaGuardada;

        // 2. Escenario de Actualización (UPDATE)
        if (citaForm.getId() != null) {
            Optional<CitaVeterinaria> citaDbOpt = citaService.findById(citaForm.getId());

            if (citaDbOpt.isPresent()) {
                CitaVeterinaria citaDb = citaDbOpt.get();

                // Mapeo manual de campos para no sobrescribir la lista de tratamientos con null
                citaDb.setFechaHora(citaForm.getFechaHora());
                citaDb.setEstado(citaForm.getEstado());
                citaDb.setMotivo(citaForm.getMotivo());
                citaDb.setDiagnostico(citaForm.getDiagnostico());
                citaDb.setMascota(citaForm.getMascota());
                citaDb.setVeterinario(citaForm.getVeterinario());

                // Persistencia manteniendo tratamientos previos
                citaGuardada = citaService.save(citaDb);

                redirect.addFlashAttribute("success", "Cita actualizada correctamente.");
                // Retorno al detalle para verificar cambios
                return "redirect:/citas/detalle/" + citaGuardada.getId();
            }
        }

        // 3. Escenario de Creación (CREATE)
        // Vinculación bidireccional preventiva si vinieran tratamientos en el form
        if (citaForm.getTratamientos() != null) {
            for (Tratamiento t : citaForm.getTratamientos()) {
                t.setCita(citaForm);
            }
        }

        citaGuardada = citaService.save(citaForm);
        redirect.addFlashAttribute("success", "Cita creada. Puede añadir tratamientos a continuación.");

        // Redirección a tratamientos tras crear una cita para agilizar el flujo.
        return "redirect:/tratamientos/cita/" + citaGuardada.getId();
    }

    /**
     * Elimina una cita del sistema.
     * @param id Identificador de la cita.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            citaService.deleteById(id);
            redirect.addFlashAttribute("success", "La cita ha sido eliminada correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al eliminar: Es posible que existan dependencias activas.");
        }
        return "redirect:/citas";
    }
}
