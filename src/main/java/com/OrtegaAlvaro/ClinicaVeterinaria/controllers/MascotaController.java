package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.MascotaDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Cliente;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Mascota;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Rol;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Usuario;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.MascotaRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.UsuarioRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.ClienteService;
import com.OrtegaAlvaro.ClinicaVeterinaria.services.MascotaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.servlet.http.HttpServletRequest;

import com.OrtegaAlvaro.ClinicaVeterinaria.services.ImagenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    @Autowired
    private ImagenService imagenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    /**
     * Lista todas las mascotas registradas.
     * GET /api/mascotas
     */
    @GetMapping
    public ResponseEntity<?> listarMascotas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String search,
            Authentication authentication) {

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();

        // CLIENTE solo ve sus propias mascotas
        if (usuario.getRol() == Rol.CLIENTE && usuario.getCliente() != null) {
            List<Mascota> misMascotas = mascotaRepository.findByClienteId(usuario.getCliente().getId());
            List<MascotaDTO> dtos = misMascotas.stream().map(this::toDTO).toList();
            return ResponseEntity.ok(new PageImpl<>(dtos));
        }

        // VETERINARIO ve todas
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Mascota> mascotas = mascotaService.findAll(pageable, search);
        return ResponseEntity.ok(mascotas.map(this::toDTO));
    }

    /**
     * Obtiene una mascota por su ID.
     * GET /api/mascotas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMascota(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
        Mascota mascota = mascotaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + id + " no existe."));

        // CLIENTE solo puede ver sus propias mascotas
        if (usuario.getRol() == Rol.CLIENTE) {
            if (usuario.getCliente() == null || !mascota.getCliente().getId().equals(usuario.getCliente().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(java.util.Map.of("error", "No tienes permiso para ver esta mascota"));
            }
        }

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
            @RequestBody MascotaDTO mascotaDTO) {

        Mascota mascotaDb = mascotaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + id + " no existe."));

        if (mascotaDTO.getNombre() != null)
            mascotaDb.setNombre(mascotaDTO.getNombre());
        if (mascotaDTO.getEspecie() != null)
            mascotaDb.setEspecie(mascotaDTO.getEspecie());
        if (mascotaDTO.getRaza() != null)
            mascotaDb.setRaza(mascotaDTO.getRaza());
        if (mascotaDTO.getFechaNacimiento() != null)
            mascotaDb.setFechaNacimiento(mascotaDTO.getFechaNacimiento());
        if (mascotaDTO.getPeso() != null)
            mascotaDb.setPeso(mascotaDTO.getPeso());
        if (mascotaDTO.getClienteId() != null) {
            Cliente cliente = clienteService.findById(mascotaDTO.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "El cliente con ID " + mascotaDTO.getClienteId() + " no existe."));
            mascotaDb.setCliente(cliente);
        }

        Mascota guardada = mascotaService.save(mascotaDb);
        return ResponseEntity.ok(toDTO(guardada));
    }

    /**
     * Elimina una mascota por su ID.
     * DELETE /api/mascotas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        Mascota mascota = mascotaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + id + " no existe."));

        if (mascota.getImagenUrl() != null && !mascota.getImagenUrl().isEmpty()) {
            String oldFileName = mascota.getImagenUrl().substring(mascota.getImagenUrl().lastIndexOf("/") + 1);
            imagenService.borrarImagen(oldFileName);
        }

        mascotaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Sube una imagen para la mascota.
     * POST /api/mascotas/{id}/imagen
     */
    @PostMapping("/{id}/imagen")
    public ResponseEntity<MascotaDTO> subirImagenMascota(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        Mascota mascota = mascotaService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La mascota con ID " + id + " no existe."));

        String fileName = imagenService.guardarImagen(file);

        // Borrar la imagen anterior si existía
        if (mascota.getImagenUrl() != null && !mascota.getImagenUrl().isEmpty()) {
            String oldFileName = mascota.getImagenUrl().substring(mascota.getImagenUrl().lastIndexOf("/") + 1);
            imagenService.borrarImagen(oldFileName);
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/mascotas/imagen/")
                .path(fileName)
                .toUriString();

        mascota.setImagenUrl(fileDownloadUri);
        Mascota guardada = mascotaService.save(mascota);

        return ResponseEntity.ok(toDTO(guardada));
    }

    /**
     * Descarga la imagen de una mascota.
     * GET /api/mascotas/imagen/{fileName:.+}
     */
    @GetMapping("/imagen/{fileName:.+}")
    public ResponseEntity<Resource> descargarImagenMascota(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = imagenService.cargarImagen(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception ex) {
            // Ignorar
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
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
        dto.setImagenUrl(m.getImagenUrl());
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
        mascota.setImagenUrl(dto.getImagenUrl());

        if (dto.getClienteId() != null) {
            Cliente cliente = clienteService.findById(dto.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "El cliente con ID " + dto.getClienteId() + " no existe."));
            mascota.setCliente(cliente);
        }
        return mascota;
    }
}