package com.OrtegaAlvaro.ClinicaVeterinaria.repositories;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de persistencia para la entidad Mascota.
 * Facilita la gestión de los pacientes de la clínica, permitiendo búsquedas
 * relacionales por propietario y filtrado demográfico por especie.
 */
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    /**
     * Recupera todas las mascotas asociadas a un cliente específico.
     * Utilizado para mostrar la lista de animales en la ficha de detalle del propietario.
     *
     * @param clienteId Identificador único del cliente (dueño).
     * @return Lista de mascotas pertenecientes a ese cliente.
     */
    List<Mascota> findByClienteId(Long clienteId);

    /**
     * Filtra las mascotas por su clasificación biológica (Especie).
     * La búsqueda es insensible a mayúsculas/minúsculas (IgnoreCase), permitiendo
     * agrupar resultados como "Perro", "perro" o "PERRO" unificadamente.
     *
     * @param especie Nombre de la especie a buscar (ej: "Gato", "Perro").
     * @return Lista de mascotas que coinciden con la especie indicada.
     */
    List<Mascota> findByEspecieIgnoreCase(String especie);
}
