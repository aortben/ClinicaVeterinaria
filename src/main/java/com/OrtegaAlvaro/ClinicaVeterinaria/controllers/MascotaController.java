package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Mascota;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.ClienteService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.MascotaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador encargado de la gestión de la entidad Mascota.
 * Administra el registro de pacientes (animales), su vinculación con los propietarios (Clientes)
 * y la actualización de sus datos clínicos básicos.
 */
@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Muestra el listado completo de mascotas registradas en el sistema.
     * Incluye la navegación hacia la ficha del propietario asociado.
     * @param model Objeto para transferir la lista a la vista.
     * @return La vista de listado de mascotas.
     */
    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.findAll());
        return "entities-html/Mascota";
    }

    /**
     * Prepara el formulario para el registro de una nueva mascota.
     * Carga la lista de clientes disponibles para poblar el selector de dueño.
     */
    @GetMapping("/nuevo")
    public String formularioCrear(Model model) {
        model.addAttribute("mascota", new Mascota());
        // Necesario para el desplegable <select> de elección de dueño
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("titulo", "Nueva Mascota");
        return "forms-html/Mascota-form";
    }

    /**
     * Prepara el formulario para la edición de una mascota existente.
     * @param id Identificador de la mascota.
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Mascota> mascotaOpt = mascotaService.findById(id);

        if (mascotaOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "La mascota solicitada no existe.");
            return "redirect:/mascotas";
        }

        model.addAttribute("mascota", mascotaOpt.get());
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("titulo", "Editar Mascota");
        return "forms-html/Mascota-form";
    }

    /**
     * Procesa la persistencia de la mascota.
     * En caso de error de validación, recarga la lista de clientes para evitar
     * que el formulario se renderice incorrectamente.
     */
    @PostMapping("/guardar")
    public String guardarMascota(
            @Valid @ModelAttribute Mascota mascota,
            BindingResult result,
            Model model,
            RedirectAttributes redirect
    ) {
        if (result.hasErrors()) {
            // Recarga crítica: Si no enviamos los clientes de nuevo, el <select> aparecerá vacío
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("titulo", mascota.getId() == null ? "Nueva Mascota" : "Editar Mascota");
            return "forms-html/Mascota-form";
        }

        mascotaService.save(mascota);
        redirect.addFlashAttribute("success", "Mascota guardada correctamente.");
        return "redirect:/mascotas";
    }

    /**
     * Elimina una mascota del sistema.
     * Controla excepciones de integridad referencial (ej: si la mascota tiene historial de citas).
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            mascotaService.deleteById(id);
            redirect.addFlashAttribute("success", "Mascota eliminada correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Operación denegada: No se puede eliminar una mascota con historial de citas activo.");
        }
        return "redirect:/mascotas";
    }
}