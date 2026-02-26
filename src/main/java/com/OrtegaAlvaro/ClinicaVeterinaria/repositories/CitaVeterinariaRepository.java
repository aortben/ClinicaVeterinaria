package com.OrtegaAlvaro.ClinicaVeterinaria.repositories;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.CitaVeterinaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad CitaVeterinaria.
 * Extiende JpaRepository para proporcionar operaciones CRUD estándar y
 * define consultas derivadas (Query Methods) para reportes y filtrado.
 */
@Repository
public interface CitaVeterinariaRepository extends JpaRepository<CitaVeterinaria, Long> {

    /**
     * Recupera el historial médico completo de una mascota específica.
     * Los resultados se ordenan cronológicamente de más reciente a más antiguo
     * (DESC),
     * facilitando la visualización de la última visita clínica.
     *
     * @param mascotaId Identificador de la mascota.
     * @return Lista de citas históricas.
     */
    List<CitaVeterinaria> findByMascotaIdOrderByFechaHoraDesc(Long mascotaId);

    /**
     * Obtiene la agenda operativa de un veterinario.
     * Los resultados se ordenan por fecha ascendente (ASC) para mostrar
     * las próximas citas a atender en primer lugar.
     *
     * @param veterinarioId Identificador del veterinario.
     * @return Lista de citas asignadas al profesional.
     */
    List<CitaVeterinaria> findByVeterinarioIdOrderByFechaHoraAsc(Long veterinarioId);

    /**
     * Filtra las citas comprendidas en un rango temporal específico.
     * Utilizado para generar vistas de agenda diaria, semanal o reportes mensuales.
     *
     * @param inicio Fecha/Hora de inicio del rango.
     * @param fin    Fecha/Hora de fin del rango.
     * @return Lista de citas dentro del intervalo.
     */
    List<CitaVeterinaria> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Recupera las 5 últimas citas registradas en el sistema a nivel global.
     * Utilizado para alimentar el widget de "Actividad Reciente" en el Dashboard
     * principal.
     *
     * @return Lista limitada a los 5 registros más recientes.
     */
    List<CitaVeterinaria> findTop5ByOrderByFechaHoraDesc();

    /**
     * Recupera todas las citas de mascotas pertenecientes a un cliente específico.
     * Utilizado para que el CLIENTE solo vea sus propias citas.
     */
    List<CitaVeterinaria> findByMascotaClienteId(Long clienteId);

    @Query("SELECT c FROM CitaVeterinaria c WHERE LOWER(c.motivo) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.diagnostico) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.estado) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<CitaVeterinaria> findBySearch(@Param("search") String search, Pageable pageable);
}
