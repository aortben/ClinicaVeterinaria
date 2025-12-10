package com.OrtegaAlvaro.ClinicaVeterinaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa al Cliente (Propietario) en el sistema.
 * Almacena la información personal de contacto y actúa como la parte "Uno"
 * en la relación 1:N con las Mascotas.
 */
@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;

    @Column(unique = true, nullable = false, length = 9)
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Za-z]$", message = "El formato debe ser 8 números seguidos de una letra")
    private String dni;

    @Column(length = 15)
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[6789][0-9]{8}$", message = "Debe introducir un número de teléfono móvil o fijo válido (9 dígitos)")
    private String telefono;

    private String direccion;

    @Email(message = "Debe proporcionar una dirección de correo electrónico válida")
    private String email;

    /**
     * Lista de mascotas asociadas a este cliente.
     * Configurada con CascadeType.ALL para que la eliminación del cliente
     * implique la eliminación de sus mascotas (Orphan Removal).
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Mascota> mascotas = new ArrayList<>();

    /**
     * Método helper para gestionar la relación bidireccional.
     * Asocia la mascota a este cliente y la añade a la lista interna.
     * @param mascota La entidad Mascota a vincular.
     */
    public void addMascota(Mascota mascota) {
        mascotas.add(mascota);
        mascota.setCliente(this);
    }
}