package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.MascotaDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Cliente;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Mascota;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.ClienteService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.MascotaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Mascotas (Pacientes).
 * Expone operaciones CRUD y vinculación con propietarios a través de la API.
 */
@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Lista todas las mascotas registradas.
     * GET /api/mascotas
     */
    @GetMapping
    public ResponseEntity<List<MascotaDTO>> listarMascotas() {
        List<MascotaDTO> dtos = mascotaService.findAll().stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene una mascota por su ID.
     * GET /api/mascotas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> obtenerMascota(@PathVariable Long id) {
        Mascota mascota = mascotaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + id + " no existe."));

        return ResponseEntity.ok(toDTO(mascota));
    }

    /**
     * Crea una nueva mascota.
     * El body JSON debe incluir un campo "clienteId" para vincularla a su
     * propietario.
     * POST /api/mascotas (body JSON con clienteId)
     */
    @PostMapping
    public ResponseEntity<MascotaDTO> crearMascota(@Valid @RequestBody MascotaDTO mascotaDTO) {
        Mascota mascota = toEntity(mascotaDTO);
        mascota.setId(null);
        Mascota guardada = mascotaService.save(mascota);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardada));
    }

    /**
     * Actualiza una mascota existente.
     * PUT /api/mascotas/{id} (body JSON con clienteId)
     */
    @PutMapping("/{id}")
    public ResponseEntity<MascotaDTO> actualizarMascota(
            @PathVariable Long id,
            @Valid @RequestBody MascotaDTO mascotaDTO) {

        mascotaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + id + " no existe."));

        Mascota mascota = toEntity(mascotaDTO);
        mascota.setId(id);
        Mascota guardada = mascotaService.save(mascota);
        return ResponseEntity.ok(toDTO(guardada));
    }

    /**
     * Elimina una mascota por su ID.
     * DELETE /api/mascotas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        mascotaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + id + " no existe."));

        mascotaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Conversiones Entity ↔ DTO ---

    private MascotaDTO toDTO(Mascota m) {
        MascotaDTO dto = new MascotaDTO();
        dto.setId(m.getId());
        dto.setNombre(m.getNombre());
        dto.setEspecie(m.getEspecie());
        dto.setRaza(m.getRaza());
        dto.setFechaNacimiento(m.getFechaNacimiento());
        dto.setPeso(m.getPeso());
        if (m.getCliente() != null) {
            dto.setClienteId(m.getCliente().getId());
            dto.setClienteNombre(m.getCliente().getNombre() + " " + m.getCliente().getApellidos());
        }
        return dto;
    }

    private Mascota toEntity(MascotaDTO dto) {
        Mascota mascota = new Mascota();
        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(dto.getEspecie());
        mascota.setRaza(dto.getRaza());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setPeso(dto.getPeso());

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteService.findById(dto.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "El cliente con ID " + dto.getClienteId() + " no existe."));
            mascota.setCliente(cliente);
        }
        return mascota;
    }
}