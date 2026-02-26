package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.ClienteDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Cliente;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Rol;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Usuario;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.UsuarioRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Lista todos los clientes o filtra por apellidos si se proporciona el
     * parámetro de búsqueda.
     * GET /api/clientes
     * GET /api/clientes?busqueda=García
     */
    @GetMapping
    public ResponseEntity<?> listarClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String search,
            Authentication authentication) {

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();

        // CLIENTE solo ve su propio perfil
        if (usuario.getRol() == Rol.CLIENTE) {
            Cliente cliente = usuario.getCliente();
            if (cliente == null)
                return ResponseEntity.ok(Page.empty());
            return ResponseEntity.ok(new PageImpl<>(List.of(toDTO(cliente))));
        }

        // VETERINARIO ve todos
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Cliente> clientes = clienteService.findAll(pageable, search);
        return ResponseEntity.ok(clientes.map(this::toDTO));
    }

    /**
     * Obtiene un cliente por su ID.
     * GET /api/clientes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCliente(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();

        // CLIENTE solo puede ver su propio perfil
        if (usuario.getRol() == Rol.CLIENTE) {
            if (usuario.getCliente() == null || !usuario.getCliente().getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(java.util.Map.of("error", "No tienes permiso para ver este perfil"));
            }
        }

        Cliente cliente = clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El cliente con ID " + id + " no existe."));
        return ResponseEntity.ok(toDTO(cliente));
    }

    /**
     * Crea un nuevo cliente.
     * POST /api/clientes (body JSON)
     */
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = toEntity(clienteDTO);
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
            @RequestBody ClienteDTO clienteDTO) {

        Cliente clienteDb = clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El cliente con ID " + id + " no existe."));

        // Solo actualizar campos que vengan en el body (no nulos)
        if (clienteDTO.getNombre() != null)
            clienteDb.setNombre(clienteDTO.getNombre());
        if (clienteDTO.getApellidos() != null)
            clienteDb.setApellidos(clienteDTO.getApellidos());
        if (clienteDTO.getDni() != null)
            clienteDb.setDni(clienteDTO.getDni());
        if (clienteDTO.getTelefono() != null)
            clienteDb.setTelefono(clienteDTO.getTelefono());
        if (clienteDTO.getDireccion() != null)
            clienteDb.setDireccion(clienteDTO.getDireccion());
        if (clienteDTO.getEmail() != null)
            clienteDb.setEmail(clienteDTO.getEmail());

        Cliente guardado = clienteService.save(clienteDb);
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

    // --- Conversiones Entity ↔ DTO ---

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

    private Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellidos(dto.getApellidos());
        cliente.setDni(dto.getDni());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setEmail(dto.getEmail());
        return cliente;
    }
}
