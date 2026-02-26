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
    @Pattern(regexp = "^[0-9]{8}[A-HJ-NP-TV-Z]$", message = "Formato de DNI inválido. Deben ser 8 números y una letra válida (no se permiten I, O, U, Ñ)")
    private String dni;

    @Column(length = 20)
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+34 ?[6789](?: ?[0-9]){8}$", message = "Debe introducir un teléfono válido con prefijo +34 (con o sin espacios) y debe empezar con 6,7,8 o 9")
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
     * 
     * @param mascota La entidad Mascota a vincular.
     */
    public void addMascota(Mascota mascota) {
        mascotas.add(mascota);
        mascota.setCliente(this);
    }

    /**
     * Devuelve el número de teléfono del cliente en un formato estandarizado.
     * El método elimina los espacios internos, asegura que el valor incluya el
     * prefijo internacional "+34" y lo presenta siempre en el formato:
     * "+34 NNNNNNNNN".
     *
     * @return El número de teléfono formateado de manera uniforme.
     */
    public String getTelefonoFormateado() {
        if (telefono == null)
            return "";

        // Eliminar todos los espacios
        String limpio = telefono.replaceAll(" ", "");

        // Asegurar prefijo +34
        if (!limpio.startsWith("+34")) {
            limpio = "+34" + limpio.replaceFirst("^\\+?34", "");
        }

        // Devolver siempre "+34 NNNNNNNNN"
        return limpio.substring(0, 3) + " " + limpio.substring(3);
    }
}