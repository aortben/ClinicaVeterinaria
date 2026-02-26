package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.CitaVeterinariaDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.dto.TratamientoDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.CitaVeterinaria;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Mascota;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Rol;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Tratamiento;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Usuario;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Veterinario;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.CitaVeterinariaRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.UsuarioRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.CitaVeterinariaService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.MascotaService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.VeterinarioService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador REST para la gestión de Citas Veterinarias.
 * Gestiona el ciclo completo: creación, consulta detallada (con tratamientos),
 * actualización y eliminación.
 */
@RestController
@RequestMapping("/api/citas")
public class CitaVeterinariaController {

    @Autowired
    private CitaVeterinariaService citaService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CitaVeterinariaRepository citaRepository;

    /**
     * Lista todas las citas registradas en el sistema.
     * GET /api/citas
     */
    @GetMapping
    public ResponseEntity<?> listarCitas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String search,
            Authentication authentication) {

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();

        // CLIENTE solo ve citas de sus mascotas
        if (usuario.getRol() == Rol.CLIENTE && usuario.getCliente() != null) {
            List<CitaVeterinaria> misCitas = citaRepository.findByMascotaClienteId(usuario.getCliente().getId());
            List<CitaVeterinariaDTO> dtos = misCitas.stream().map(this::toDTO).toList();
            return ResponseEntity.ok(new PageImpl<>(dtos));
        }

        // VETERINARIO ve todas
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<CitaVeterinaria> citas = citaService.findAll(pageable, search);
        return ResponseEntity.ok(citas.map(this::toDTO));
    }

    /**
     * Obtiene el detalle completo de una cita (incluidos tratamientos).
     * GET /api/citas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCita(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
        CitaVeterinaria cita = citaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La cita con ID " + id + " no existe."));

        // CLIENTE solo puede ver citas de sus mascotas
        if (usuario.getRol() == Rol.CLIENTE) {
            if (usuario.getCliente() == null
                    || !cita.getMascota().getCliente().getId().equals(usuario.getCliente().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(java.util.Map.of("error", "No tienes permiso para ver esta cita"));
            }
        }

        return ResponseEntity.ok(toDTO(cita));
    }

    /**
     * Crea una nueva cita veterinaria. Solo VETERINARIO.
     * El body JSON debe incluir mascotaId y veterinarioId.
     * POST /api/citas
     */
    @PostMapping
    public ResponseEntity<CitaVeterinariaDTO> crearCita(
            @Valid @RequestBody CitaVeterinariaDTO citaDTO) {

        CitaVeterinaria cita = toEntity(citaDTO);
        cita.setId(null);
        cita.setTratamientos(new ArrayList<>());

        CitaVeterinaria guardada = citaService.save(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardada));
    }

    /**
     * Actualiza una cita existente.
     * Preserva los tratamientos existentes (no se modifican desde este endpoint).
     * PUT /api/citas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CitaVeterinariaDTO> actualizarCita(
            @PathVariable Long id,
            @RequestBody CitaVeterinariaDTO citaDTO) {

        CitaVeterinaria citaDb = citaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "La cita con ID " + id + " no existe."));

        if (citaDTO.getFechaHora() != null)
            citaDb.setFechaHora(citaDTO.getFechaHora());
        if (citaDTO.getMotivo() != null)
            citaDb.setMotivo(citaDTO.getMotivo());
        if (citaDTO.getDiagnostico() != null)
            citaDb.setDiagnostico(citaDTO.getDiagnostico());
        if (citaDTO.getEstado() != null)
            citaDb.setEstado(citaDTO.getEstado());
        if (citaDTO.getMascotaId() != null) {
            Mascota mascota = mascotaService.findById(citaDTO.getMascotaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "La mascota con ID " + citaDTO.getMascotaId() + " no existe."));
            citaDb.setMascota(mascota);
        }
        if (citaDTO.getVeterinarioId() != null) {
            Veterinario vet = veterinarioService.findById(citaDTO.getVeterinarioId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "El veterinario con ID " + citaDTO.getVeterinarioId() + " no existe."));
            citaDb.setVeterinario(vet);
        }

        CitaVeterinaria guardada = citaService.save(citaDb);
        return ResponseEntity.ok(toDTO(guardada));
    }

    /**
     * Elimina una cita por su ID.
     * DELETE /api/citas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        citaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "La cita con ID " + id + " no existe."));

        citaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Conversiones Entity ↔ DTO ---

    private CitaVeterinariaDTO toDTO(CitaVeterinaria c) {
        CitaVeterinariaDTO dto = new CitaVeterinariaDTO();
        dto.setId(c.getId());
        dto.setFechaHora(c.getFechaHora());
        dto.setMotivo(c.getMotivo());
        dto.setDiagnostico(c.getDiagnostico());
        dto.setEstado(c.getEstado());
        dto.setCosteTotal(c.getCosteTotal());

        if (c.getMascota() != null) {
            dto.setMascotaId(c.getMascota().getId());
            dto.setMascotaNombre(c.getNombreMascotaStr());
        }
        if (c.getVeterinario() != null) {
            dto.setVeterinarioId(c.getVeterinario().getId());
            dto.setVeterinarioNombre(c.getNombreVeterinarioStr());
        }

        if (c.getTratamientos() != null) {
            dto.setTratamientos(c.getTratamientos().stream()
                    .map(this::tratamientoToDTO)
                    .toList());
        } else {
            dto.setTratamientos(Collections.emptyList());
        }

        return dto;
    }

    private TratamientoDTO tratamientoToDTO(Tratamiento t) {
        TratamientoDTO dto = new TratamientoDTO();
        dto.setId(t.getId());
        dto.setDescripcion(t.getDescripcion());
        dto.setMedicamento(t.getMedicamento());
        dto.setPrecio(t.getPrecio());
        dto.setObservaciones(t.getObservaciones());
        dto.setCitaId(t.getCita() != null ? t.getCita().getId() : null);
        return dto;
    }

    private CitaVeterinaria toEntity(CitaVeterinariaDTO dto) {
        CitaVeterinaria cita = new CitaVeterinaria();
        cita.setFechaHora(dto.getFechaHora());
        cita.setMotivo(dto.getMotivo());
        cita.setDiagnostico(dto.getDiagnostico());
        cita.setEstado(dto.getEstado());

        if (dto.getMascotaId() != null) {
            Mascota mascota = mascotaService.findById(dto.getMascotaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "La mascota con ID " + dto.getMascotaId() + " no existe."));
            cita.setMascota(mascota);
        }

        if (dto.getVeterinarioId() != null) {
            Veterinario vet = veterinarioService.findById(dto.getVeterinarioId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "El veterinario con ID " + dto.getVeterinarioId() + " no existe."));
            cita.setVeterinario(vet);
        }

        return cita;
    }
}
