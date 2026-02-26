package com.OrtegaAlvaro.ClinicaVeterinaria.entities;

/**
 * Roles de usuario del sistema.
 * VETERINARIO: acceso completo al CRUD.
 * CLIENTE: puede concertar y consultar sus propias citas.
 */
public enum Rol {
    VETERINARIO,
    CLIENTE
}
