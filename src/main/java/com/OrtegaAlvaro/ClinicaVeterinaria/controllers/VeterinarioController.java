package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Veterinario;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador encargado de la gestión del personal médico (Veterinarios).
 * Administra el alta de profesionales, asignación de especialidades y datos de colegiación.
 */
@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    /**
     * Carga la lista de especialidades disponibles en el sistema.
     * Al usar @ModelAttribute, esta lista está disponible automáticamente en
     * todas las vistas devueltas por este controlador (formularios de creación y edición).
     */
    @ModelAttribute("listaEspecialidades")
    public List<String> getEspecialidades() {
        return List.of(
                "Medicina General",
                "Cirugía",
                "Dermatología",
                "Traumatología",
                "Exóticos",
                "Cardiología",
                "Oncología",
                "Odontología"
        );
    }

    /**
     * Muestra el listado del equipo veterinario registrado.
     * @param model Objeto para transferir datos a la vista.
     * @return Vista del listado de veterinarios.
     */
    @GetMapping
    public String listarVeterinarios(Model model) {
        model.addAttribute("veterinarios", veterinarioService.findAll());
        return "entities-html/Veterinario";
    }

    /**
     * Prepara el formulario para el alta de un nuevo profesional.
     */
    @GetMapping("/nuevo")
    public String formularioCrear(Model model) {
        model.addAttribute("veterinario", new Veterinario());
        model.addAttribute("titulo", "Nuevo Veterinario");
        return "forms-html/Veterinario-form";
    }

    /**
     * Prepara el formulario para la edición de los datos de un veterinario.
     * @param id Identificador del veterinario.
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Veterinario> vetOpt = veterinarioService.findById(id);

        if (vetOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "El veterinario solicitado no existe en la base de datos.");
            return "redirect:/veterinarios";
        }

        model.addAttribute("veterinario", vetOpt.get());
        model.addAttribute("titulo", "Editar Veterinario");
        return "forms-html/Veterinario-form";
    }

    /**
     * Procesa la persistencia del veterinario.
     * Controla la unicidad del número de colegiado mediante captura de excepciones.
     */
    @PostMapping("/guardar")
    public String guardarVeterinario(
            @Valid @ModelAttribute Veterinario veterinario,
            BindingResult result,
            Model model,
            RedirectAttributes redirect
    ) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", veterinario.getId() == null ? "Nuevo Veterinario" : "Editar Veterinario");
            return "forms-html/Veterinario-form";
        }

        try {
            veterinarioService.save(veterinario);
            redirect.addFlashAttribute("success", "Datos del veterinario guardados correctamente.");
        } catch (Exception e) {
            // Captura de excepción por restricción UNIQUE en base de datos (Nº Colegiado)
            model.addAttribute("error", "Error de validación: El número de colegiado ya está registrado en el sistema.");
            model.addAttribute("titulo", veterinario.getId() == null ? "Nuevo Veterinario" : "Editar Veterinario");
            return "forms-html/Veterinario-form";
        }

        return "redirect:/veterinarios";
    }

    /**
     * Elimina un veterinario del sistema.
     * La operación será denegada si el veterinario tiene citas históricas o activas asignadas.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarVeterinario(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            veterinarioService.deleteById(id);
            redirect.addFlashAttribute("success", "Veterinario eliminado correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Operación denegada: El veterinario tiene historial de citas asociado.");
        }
        return "redirect:/veterinarios";
    }
}