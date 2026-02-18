package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.VeterinarioDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Veterinario;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.VeterinarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del Personal Veterinario.
 * Expone operaciones CRUD y la lista de especialidades disponibles.
 */
@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    /**
     * Devuelve la lista de especialidades disponibles en el sistema.
     * GET /api/veterinarios/especialidades
     */
    @GetMapping("/especialidades")
    public ResponseEntity<List<String>> getEspecialidades() {
        List<String> especialidades = List.of(
                "Medicina General",
                "Cirugía",
                "Dermatología",
                "Traumatología",
                "Exóticos",
                "Cardiología",
                "Oncología",
                "Odontología");
        return ResponseEntity.ok(especialidades);
    }

    /**
     * Lista todos los veterinarios registrados.
     * GET /api/veterinarios
     */
    @GetMapping
    public ResponseEntity<List<VeterinarioDTO>> listarVeterinarios() {
        List<VeterinarioDTO> dtos = veterinarioService.findAll().stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene un veterinario por su ID.
     * GET /api/veterinarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> obtenerVeterinario(@PathVariable Long id) {
        Veterinario vet = veterinarioService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El veterinario con ID " + id + " no existe."));

        return ResponseEntity.ok(toDTO(vet));
    }

    /**
     * Crea un nuevo veterinario.
     * POST /api/veterinarios (body JSON)
     */
    @PostMapping
    public ResponseEntity<VeterinarioDTO> crearVeterinario(
            @Valid @RequestBody Veterinario veterinario) {

        veterinario.setId(null);
        Veterinario guardado = veterinarioService.save(veterinario);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardado));
    }

    /**
     * Actualiza un veterinario existente.
     * PUT /api/veterinarios/{id} (body JSON)
     */
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> actualizarVeterinario(
            @PathVariable Long id,
            @Valid @RequestBody Veterinario veterinario) {

        veterinarioService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El veterinario con ID " + id + " no existe."));

        veterinario.setId(id);
        Veterinario guardado = veterinarioService.save(veterinario);
        return ResponseEntity.ok(toDTO(guardado));
    }

    /**
     * Elimina un veterinario por su ID.
     * DELETE /api/veterinarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVeterinario(@PathVariable Long id) {
        veterinarioService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El veterinario con ID " + id + " no existe."));

        veterinarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Conversión Entity → DTO ---

    private VeterinarioDTO toDTO(Veterinario v) {
        VeterinarioDTO dto = new VeterinarioDTO();
        dto.setId(v.getId());
        dto.setNombre(v.getNombre());
        dto.setApellidos(v.getApellidos());
        dto.setNumeroColegiado(v.getNumeroColegiado());
        dto.setEspecialidad(v.getEspecialidad());
        dto.setEmail(v.getEmail());
        return dto;
    }
}