package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.TratamientoDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.CitaVeterinaria;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Rol;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Tratamiento;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Usuario;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.TratamientoRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.UsuarioRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.CitaVeterinariaService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.TratamientoService;
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
 * Controlador REST para la gestión de Tratamientos y Servicios clínicos.
 * Expone operaciones CRUD y filtrado por cita a través de la API.
 */
@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private CitaVeterinariaService citaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    /**
     * Lista todos los tratamientos del sistema.
     * GET /api/tratamientos
     */
    @GetMapping
    public ResponseEntity<?> listarGlobal(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String search,
            Authentication authentication) {

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();

        // CLIENTE solo ve tratamientos de sus mascotas
        if (usuario.getRol() == Rol.CLIENTE && usuario.getCliente() != null) {
            List<Tratamiento> misTratamientos = tratamientoRepository
                    .findByCitaMascotaClienteId(usuario.getCliente().getId());
            List<TratamientoDTO> dtos = misTratamientos.stream().map(this::toDTO).toList();
            return ResponseEntity.ok(new PageImpl<>(dtos));
        }

        // VETERINARIO ve todos
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Tratamiento> tratamientos = tratamientoService.findAll(pageable, search);
        return ResponseEntity.ok(tratamientos.map(this::toDTO));
    }

    /**
     * Lista los tratamientos de una cita específica.
     * GET /api/tratamientos/cita/{citaId}
     */
    @GetMapping("/cita/{citaId}")
    public ResponseEntity<List<TratamientoDTO>> listarPorCita(@PathVariable Long citaId) {
        citaService.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "La cita con ID " + citaId + " no existe."));

        List<TratamientoDTO> dtos = tratamientoService.findByCitaId(citaId).stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtiene un tratamiento por su ID.
     * GET /api/tratamientos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TratamientoDTO> obtenerTratamiento(@PathVariable Long id) {
        Tratamiento trat = tratamientoService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El tratamiento con ID " + id + " no existe."));

        return ResponseEntity.ok(toDTO(trat));
    }

    /**
     * Crea un nuevo tratamiento vinculado a una cita.
     * El body JSON debe incluir citaId.
     * POST /api/tratamientos
     */
    @PostMapping
    public ResponseEntity<TratamientoDTO> crearTratamiento(
            @Valid @RequestBody TratamientoDTO tratamientoDTO) {

        Tratamiento trat = toEntity(tratamientoDTO);
        trat.setId(null);
        Tratamiento guardado = tratamientoService.save(trat);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardado));
    }

    /**
     * Actualiza un tratamiento existente.
     * PUT /api/tratamientos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TratamientoDTO> actualizarTratamiento(
            @PathVariable Long id,
            @RequestBody TratamientoDTO tratamientoDTO) {

        Tratamiento tratDb = tratamientoService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El tratamiento con ID " + id + " no existe."));

        if (tratamientoDTO.getDescripcion() != null)
            tratDb.setDescripcion(tratamientoDTO.getDescripcion());
        if (tratamientoDTO.getMedicamento() != null)
            tratDb.setMedicamento(tratamientoDTO.getMedicamento());
        if (tratamientoDTO.getPrecio() != null)
            tratDb.setPrecio(tratamientoDTO.getPrecio());
        if (tratamientoDTO.getObservaciones() != null)
            tratDb.setObservaciones(tratamientoDTO.getObservaciones());
        if (tratamientoDTO.getCitaId() != null) {
            CitaVeterinaria cita = citaService.findById(tratamientoDTO.getCitaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "La cita con ID " + tratamientoDTO.getCitaId() + " no existe."));
            tratDb.setCita(cita);
        }

        Tratamiento guardado = tratamientoService.save(tratDb);
        return ResponseEntity.ok(toDTO(guardado));
    }

    /**
     * Elimina un tratamiento por su ID.
     * DELETE /api/tratamientos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTratamiento(@PathVariable Long id) {
        tratamientoService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El tratamiento con ID " + id + " no existe."));

        tratamientoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Conversiones Entity ↔ DTO ---

    private TratamientoDTO toDTO(Tratamiento t) {
        TratamientoDTO dto = new TratamientoDTO();
        dto.setId(t.getId());
        dto.setDescripcion(t.getDescripcion());
        dto.setMedicamento(t.getMedicamento());
        dto.setPrecio(t.getPrecio());
        dto.setObservaciones(t.getObservaciones());
        dto.setCitaId(t.getCita() != null ? t.getCita().getId() : null);
        return dto;
    }

    private Tratamiento toEntity(TratamientoDTO dto) {
        Tratamiento trat = new Tratamiento();
        trat.setDescripcion(dto.getDescripcion());
        trat.setMedicamento(dto.getMedicamento());
        trat.setPrecio(dto.getPrecio());
        trat.setObservaciones(dto.getObservaciones());

        if (dto.getCitaId() != null) {
            CitaVeterinaria cita = citaService.findById(dto.getCitaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "La cita con ID " + dto.getCitaId() + " no existe."));
            trat.setCita(cita);
        }
        return trat;
    }
}