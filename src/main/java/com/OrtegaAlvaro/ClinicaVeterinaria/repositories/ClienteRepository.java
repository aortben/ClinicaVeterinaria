package com.OrtegaAlvaro.ClinicaVeterinaria.repositories;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de persistencia para la entidad Cliente.
 * Gestiona las operaciones de base de datos relacionadas con los propietarios de mascotas,
 * incluyendo búsquedas exactas por identificación legal y búsquedas aproximadas por nombre.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Recupera un cliente único basado en su Documento Nacional de Identidad.
     * Utilizado para validaciones de unicidad y búsquedas directas en procesos administrativos.
     *
     * @param dni El DNI exacto a buscar.
     * @return Un contenedor Optional con el cliente si existe, o vacío si no.
     */
    Optional<Cliente> findByDni(String dni);

    /**
     * Realiza una búsqueda flexible de clientes por sus apellidos.
     * La consulta contiene una cláusula 'LIKE' implícita y es insensible a mayúsculas/minúsculas,
     * lo que la hace ideal para barras de búsqueda en tiempo real o filtrado de listados.
     *
     * @param apellidos Fragmento de texto a buscar dentro del campo apellidos.
     * @return Lista de clientes que coinciden con el criterio.
     */
    List<Cliente> findByApellidosContainingIgnoreCase(String apellidos);
}
