package com.OrtegaAlvaro.ClinicaVeterinaria.services;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Tratamiento;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.TratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión de Tratamientos (Servicios Clínicos).
 * Administra el ciclo de vida de los ítems facturables o procedimientos médicos
 * realizados durante una Cita Veterinaria.
 */
@Service
public class TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    /**
     * Recupera el histórico global de tratamientos realizados.
     * Útil para auditorías o reportes generales de actividad.
     * @return Lista de todos los tratamientos.
     */
    public List<Tratamiento> findAll() {
        return tratamientoRepository.findAll();
    }

    /**
     * Busca un tratamiento específico por su identificador.
     * @param id Identificador del tratamiento.
     * @return Contenedor Optional con el tratamiento si existe.
     */
    public Optional<Tratamiento> findById(Long id) {
        return tratamientoRepository.findById(id);
    }

    /**
     * Obtiene todos los tratamientos asociados a una cita concreta.
     * Este método es fundamental para visualizar el detalle económico (factura)
     * y el desglose de servicios dentro de la ficha de la cita.
     * @param citaId Identificador de la cita padre.
     */
    public List<Tratamiento> findByCitaId(Long citaId) {
        return tratamientoRepository.findByCitaId(citaId);
    }

    /**
     * Persiste (Crea o Actualiza) un tratamiento.
     * @param tratamiento Entidad con los datos del servicio a guardar.
     * @return El tratamiento persistido.
     */
    public Tratamiento save(Tratamiento tratamiento) {
        return tratamientoRepository.save(tratamiento);
    }

    /**
     * Elimina un tratamiento o línea de servicio.
     * @param id Identificador del tratamiento a borrar.
     */
    public void deleteById(Long id) {
        tratamientoRepository.deleteById(id);
    }
}