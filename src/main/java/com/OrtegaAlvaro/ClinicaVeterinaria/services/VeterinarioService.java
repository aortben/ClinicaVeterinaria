package com.OrtegaAlvaro.ClinicaVeterinaria.services;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Veterinario;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.VeterinarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión del Personal Veterinario.
 * Actúa como fachada para las operaciones CRUD sobre la entidad Veterinario,
 * centralizando la gestión de los recursos humanos médicos de la clínica.
 */
@Service
public class VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    /**
     * Recupera el cuadro médico completo de la clínica.
     * @return Lista de todos los veterinarios registrados.
     */
    public List<Veterinario> findAll() {
        return veterinarioRepository.findAll();
    }

    /**
     * Busca un profesional específico por su identificador único.
     * @param id Identificador del veterinario.
     * @return Contenedor Optional con el veterinario si existe.
     */
    public Optional<Veterinario> findById(Long id) {
        return veterinarioRepository.findById(id);
    }

    /**
     * Persiste (Alta o Modificación) los datos de un profesional veterinario.
     * @param veterinario Entidad con los datos a guardar.
     * @return El veterinario persistido.
     */
    public Veterinario save(Veterinario veterinario) {
        return veterinarioRepository.save(veterinario);
    }

    /**
     * Elimina un veterinario del sistema.
     * La integridad referencial (citas asociadas) debe ser gestionada
     * previamente o controlada mediante excepciones en la capa superior.
     * @param id Identificador del veterinario a eliminar.
     */
    public void deleteById(Long id) {
        veterinarioRepository.deleteById(id);
    }
}