package com.OrtegaAlvaro.ClinicaVeterinaria.services;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Mascota;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión de Mascotas (Pacientes).
 * Centraliza las operaciones CRUD sobre los animales y gestiona las consultas
 * relacionadas con la propiedad (vínculo con Clientes).
 */
@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    /**
     * Recupera el censo completo de mascotas registradas en el sistema.
     * 
     * @return Lista de todos los animales.
     */
    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    public Page<Mascota> findAll(Pageable pageable, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return mascotaRepository.findBySearch(search, pageable);
        }
        return mascotaRepository.findAll(pageable);
    }

    /**
     * Busca una mascota específica por su identificador único.
     * 
     * @param id Identificador de la mascota.
     * @return Contenedor Optional con la mascota si existe.
     */
    public Optional<Mascota> findById(Long id) {
        return mascotaRepository.findById(id);
    }

    /**
     * Obtiene la lista de mascotas pertenecientes a un cliente específico.
     * Esencial para la vista de detalle del cliente, donde se muestran sus
     * animales.
     * 
     * @param clienteId Identificador del dueño.
     */
    public List<Mascota> findByClienteId(Long clienteId) {
        return mascotaRepository.findByClienteId(clienteId);
    }

    /**
     * Persiste (Crea o Actualiza) los datos clínicos de una mascota.
     * 
     * @param mascota Entidad con los datos a guardar.
     * @return La mascota persistida.
     */
    public Mascota save(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    /**
     * Elimina una mascota del sistema.
     * 
     * @param id Identificador de la mascota a dar de baja.
     */
    public void deleteById(Long id) {
        mascotaRepository.deleteById(id);
    }
}