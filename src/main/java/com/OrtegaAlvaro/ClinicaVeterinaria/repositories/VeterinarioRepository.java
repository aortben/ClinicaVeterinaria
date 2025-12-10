package com.OrtegaAlvaro.ClinicaVeterinaria.repositories;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de persistencia para la entidad Veterinario.
 * Gestiona el acceso a datos del personal médico, permitiendo la segmentación del equipo
 * por especialidad clínica y la búsqueda por identidad.
 */
@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    /**
     * Filtra la lista de veterinarios según su área de especialización médica.
     * Es fundamental para asignar citas correctamente según la patología de la mascota
     * (Ej: Asignar un caso de piel a "Dermatología").
     *
     * @param especialidad Nombre exacto de la especialidad.
     * @return Lista de profesionales que ejercen dicha especialidad.
     */
    List<Veterinario> findByEspecialidad(String especialidad);

    /**
     * Realiza una búsqueda flexible de veterinarios por apellidos.
     * La consulta es insensible a mayúsculas/minúsculas y permite coincidencias parciales.
     * Ideal para funcionalidades de búsqueda rápida en la gestión administrativa.
     *
     * @param apellidos Fragmento del apellido a buscar.
     * @return Lista de veterinarios que coinciden con el criterio.
     */
    List<Veterinario> findByApellidosContainingIgnoreCase(String apellidos);
}
