package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.ClienteDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Cliente;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Clientes (Propietarios).
 * Expone operaciones CRUD y búsqueda por apellidos a través de la API.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Lista todos los clientes o filtra por apellidos si se proporciona el
     * parámetro de búsqueda.
     * GET /api/clientes
     * GET /api/clientes?busqueda=García
     */
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes(
            @RequestParam(required = false) String busqueda) {

        List<Cliente> clientes;
        if (busqueda != null && !busqueda.isEmpty()) {
            clientes = clienteService.buscarPorApellidos(busqueda);
        } else {
            clientes = clienteService.findAll();
        }

        List<ClienteDTO> dtos = clientes.stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene un cliente por su ID.
     * GET /api/clientes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El cliente con ID " + id + " no existe."));

        return ResponseEntity.ok(toDTO(cliente));
    }

    /**
     * Crea un nuevo cliente.
     * POST /api/clientes (body JSON)
     */
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody Cliente cliente) {
        cliente.setId(null); // Asegurar creación, no actualización
        Cliente guardado = clienteService.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardado));
    }

    /**
     * Actualiza un cliente existente.
     * PUT /api/clientes/{id} (body JSON)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {

        clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El cliente con ID " + id + " no existe."));

        cliente.setId(id);
        Cliente guardado = clienteService.save(cliente);
        return ResponseEntity.ok(toDTO(guardado));
    }

    /**
     * Elimina un cliente por su ID.
     * DELETE /api/clientes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El cliente con ID " + id + " no existe."));

        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Conversión Entity → DTO ---

    private ClienteDTO toDTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setApellidos(c.getApellidos());
        dto.setDni(c.getDni());
        dto.setTelefono(c.getTelefono());
        dto.setTelefonoFormateado(c.getTelefonoFormateado());
        dto.setDireccion(c.getDireccion());
        dto.setEmail(c.getEmail());
        return dto;
    }
}
