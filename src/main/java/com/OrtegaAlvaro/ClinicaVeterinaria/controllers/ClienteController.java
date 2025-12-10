package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Cliente;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.ClienteService;
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
 * Controlador encargado de la gestión de la entidad Cliente.
 * Administra el registro de propietarios, actualizaciones de datos de contacto
 * y visualización del listado general.
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Muestra el listado paginado o filtrado de clientes.
     * Incorpora lógica de búsqueda por coincidencia de apellidos.
     * @param busqueda Cadena de texto para filtrar resultados (opcional).
     * @param model Objeto para transferir datos a la vista.
     * @return Vista del listado de clientes.
     */
    @GetMapping
    public String listarClientes(@RequestParam(required = false) String busqueda, Model model) {
        List<Cliente> clientes;

        if (busqueda != null && !busqueda.isEmpty()) {
            clientes = clienteService.buscarPorApellidos(busqueda);
        } else {
            clientes = clienteService.findAll();
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("busqueda", busqueda);
        return "entities-html/Cliente";
    }

    /**
     * Prepara el formulario para el alta de un nuevo cliente.
     */
    @GetMapping("/nuevo")
    public String formularioCrear(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo Cliente");
        return "forms-html/Cliente-form";
    }

    /**
     * Prepara el formulario para la modificación de los datos de un cliente existente.
     * @param id Identificador del cliente.
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Cliente> clienteOpt = clienteService.findById(id);

        if (clienteOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "El cliente solicitado no existe.");
            return "redirect:/clientes";
        }

        model.addAttribute("cliente", clienteOpt.get());
        model.addAttribute("titulo", "Editar Cliente");
        return "forms-html/Cliente-form";
    }

    /**
     * Procesa la persistencia del cliente (Creación o Actualización).
     * Maneja excepciones de integridad de datos (ej: DNI duplicado).
     */
    @PostMapping("/guardar")
    public String guardarCliente(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult result,
            Model model,
            RedirectAttributes redirect
    ) {
        // 1. Validación de campos (JSR-303)
        if (result.hasErrors()) {
            model.addAttribute("titulo", cliente.getId() == null ? "Nuevo Cliente" : "Editar Cliente");
            return "forms-html/Cliente-form";
        }

        // 2. Intento de persistencia
        try {
            clienteService.save(cliente);
            redirect.addFlashAttribute("success", "Cliente guardado correctamente.");
        } catch (Exception e) {
            // Captura de error SQL (Unique Constraint) si el DNI ya existe
            model.addAttribute("error", "Error al guardar: El DNI introducido ya pertenece a otro cliente.");
            model.addAttribute("titulo", cliente.getId() == null ? "Nuevo Cliente" : "Editar Cliente");
            return "forms-html/Cliente-form";
        }

        return "redirect:/clientes";
    }

    /**
     * Elimina un cliente del sistema.
     * Controla la integridad referencial para evitar borrar clientes con mascotas activas.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            clienteService.deleteById(id);
            redirect.addFlashAttribute("success", "Cliente eliminado correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Operación denegada: El cliente tiene mascotas o historial asociado.");
        }
        return "redirect:/clientes";
    }
}
