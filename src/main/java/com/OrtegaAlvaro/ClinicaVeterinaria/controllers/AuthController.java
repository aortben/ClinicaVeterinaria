package com.OrtegaAlvaro.ClinicaVeterinaria.controllers;

import com.OrtegaAlvaro.ClinicaVeterinaria.dto.AuthResponseDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.dto.LoginDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.dto.RegistroDTO;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Cliente;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Rol;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Usuario;
import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Veterinario;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.ClienteRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.UsuarioRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.VeterinarioRepository;
import com.OrtegaAlvaro.ClinicaVeterinaria.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroDTO request) {

        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build(); // Email ya registrado
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());

        if (request.getRol() == Rol.CLIENTE) {
            Cliente cliente = new Cliente();
            cliente.setNombre(request.getNombre());
            cliente.setApellidos(request.getApellidos());
            cliente.setDni(request.getDni());
            cliente.setTelefono(request.getTelefono());
            cliente.setDireccion(request.getDireccion());
            cliente = clienteRepository.save(cliente);
            usuario.setCliente(cliente);
        } else if (request.getRol() == Rol.VETERINARIO) {
            Veterinario veterinario = new Veterinario();
            veterinario.setNombre(request.getNombre());
            veterinario.setApellidos(request.getApellidos());
            veterinario.setNumeroColegiado(request.getNumeroColegiado());
            veterinario.setEmail(request.getEmail());
            veterinario.setEspecialidad(request.getEspecialidad());
            veterinario = veterinarioRepository.save(veterinario);
            usuario.setVeterinario(veterinario);
        }

        usuarioRepository.save(usuario);

        String jwtToken = jwtService.generateToken(usuario);
        return ResponseEntity.ok(new AuthResponseDTO(jwtToken, usuario.getEmail(), usuario.getRol()));
    }

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
            String jwtToken = jwtService.generateToken(usuario);
            return ResponseEntity.ok(new AuthResponseDTO(jwtToken, usuario.getEmail(), usuario.getRol()));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of(
                            "error", e.getClass().getName(),
                            "mensaje", e.getMessage() != null ? e.getMessage() : "sin mensaje",
                            "causa", e.getCause() != null ? e.getCause().toString() : "sin causa"));
        }
    }
}
