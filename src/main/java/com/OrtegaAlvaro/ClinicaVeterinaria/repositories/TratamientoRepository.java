package com.OrtegaAlvaro.ClinicaVeterinaria.repositories;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio de persistencia para la entidad Tratamiento.
 * Gestiona el acceso a las líneas de servicio o ítems facturables asociados a
 * las citas.
 * Permite realizar análisis de servicios prestados y consultas de facturación.
 */
@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    /**
     * Realiza una búsqueda flexible de tratamientos por su descripción.
     * Útil para auditar qué tipos de servicios se están realizando (ej: buscar
     * todas las "Vacunas").
     * La búsqueda es insensible a mayúsculas/minúsculas.
     *
     * @param texto Fragmento de texto a buscar.
     * @return Lista de tratamientos que contienen el texto indicado.
     */
    List<Tratamiento> findByDescripcionContainingIgnoreCase(String texto);

    /**
     * Filtra tratamientos cuyo coste sea igual o inferior al precio indicado.
     * Utilizado para reportes económicos o filtrado por rango de presupuesto.
     *
     * @param precioMaximo Límite superior de precio.
     * @return Lista de tratamientos dentro del rango económico.
     */
    List<Tratamiento> findByPrecioLessThanEqual(Double precioMaximo);

    /**
     * Recupera todos los servicios o tratamientos asociados a una cita específica.
     * Es la consulta principal para construir la "factura" o detalle de la visita
     * médica.
     *
     * @param citaId Identificador único de la cita veterinaria.
     * @return Lista de tratamientos vinculados a esa cita.
     */
    List<Tratamiento> findByCitaId(Long citaId);

    /**
     * Recupera todos los tratamientos de mascotas pertenecientes a un cliente.
     * Utilizado para que el CLIENTE solo vea los tratamientos de sus mascotas.
     */
    List<Tratamiento> findByCitaMascotaClienteId(Long clienteId);

    @Query("SELECT t FROM Tratamiento t WHERE LOWER(t.descripcion) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.medicamento) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Tratamiento> findBySearch(@Param("search") String search, Pageable pageable);
}