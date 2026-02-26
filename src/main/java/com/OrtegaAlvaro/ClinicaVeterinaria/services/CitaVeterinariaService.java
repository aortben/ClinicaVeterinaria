package com.OrtegaAlvaro.ClinicaVeterinaria.services;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.CitaVeterinaria;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.CitaVeterinariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión de Citas Veterinarias.
 * Encapsula las operaciones de acceso a datos y actúa como intermediario
 * entre la capa de presentación (Controladores) y la capa de persistencia
 * (Repositorios).
 */
@Service
public class CitaVeterinariaService {

    @Autowired
    private CitaVeterinariaRepository citaRepository;

    /**
     * Recupera el catálogo completo de citas registradas.
     * 
     * @return Lista de todas las citas.
     */
    public List<CitaVeterinaria> findAll() {
        return citaRepository.findAll();
    }

    public Page<CitaVeterinaria> findAll(Pageable pageable, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return citaRepository.findBySearch(search, pageable);
        }
        return citaRepository.findAll(pageable);
    }

    /**
     * Busca una cita específica por su identificador único.
     * 
     * @param id Identificador de la cita.
     * @return Contenedor Optional con la cita si existe.
     */
    public Optional<CitaVeterinaria> findById(Long id) {
        return citaRepository.findById(id);
    }

    /**
     * Persiste (Crea o Actualiza) una cita en la base de datos.
     * 
     * @param cita La entidad a guardar.
     * @return La entidad persistida (incluyendo su ID generado si es nueva).
     */
    public CitaVeterinaria save(CitaVeterinaria cita) {
        return citaRepository.save(cita);
    }

    /**
     * Elimina permanentemente una cita del sistema.
     * 
     * @param id Identificador de la cita a eliminar.
     */
    public void deleteById(Long id) {
        citaRepository.deleteById(id);
    }

    // --- MÉTODOS DE NEGOCIO ESPECÍFICOS ---

    /**
     * Recupera el historial clínico completo de una mascota.
     * Ordenado cronológicamente descendente (lo más reciente primero).
     * 
     * @param mascotaId ID del paciente (mascota).
     */
    public List<CitaVeterinaria> buscarPorMascota(Long mascotaId) {
        // Nota: Llamada actualizada para coincidir con la normalización del Repositorio
        return citaRepository.findByMascotaIdOrderByFechaHoraDesc(mascotaId);
    }

    /**
     * Obtiene la agenda de trabajo de un veterinario.
     * Ordenado cronológicamente ascendente (próximas citas primero).
     * 
     * @param veterinarioId ID del profesional.
     */
    public List<CitaVeterinaria> buscarPorVeterinario(Long veterinarioId) {
        // Nota: Llamada actualizada para coincidir con la normalización del Repositorio
        return citaRepository.findByVeterinarioIdOrderByFechaHoraAsc(veterinarioId);
    }

    /**
     * Filtra las citas existentes dentro de un rango de fechas.
     * Utilizado para reportes de actividad o visualización de agenda
     * diaria/semanal.
     * 
     * @param inicio Fecha de inicio.
     * @param fin    Fecha de fin.
     */
    public List<CitaVeterinaria> buscarEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.findByFechaHoraBetween(inicio, fin);
    }
}