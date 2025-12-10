package com.OrtegaAlvaro.ClinicaVeterinaria.services;

import com.OrtegaAlvaro.ClinicaVeterinaria.entities.Cliente;
import com.OrtegaAlvaro.ClinicaVeterinaria.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión de Clientes (Propietarios).
 * Centraliza las operaciones CRUD y las consultas específicas de búsqueda
 * antes de interactuar con la capa de persistencia.
 */
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtiene el listado completo de clientes registrados en el sistema.
     * @return Lista de todos los clientes.
     */
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    /**
     * Recupera un cliente por su identificador único de base de datos.
     * @param id Identificador del cliente.
     * @return Contenedor Optional con el cliente si existe.
     */
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Busca un cliente por su Documento Nacional de Identidad.
     * Fundamental para validar duplicados antes de crear un nuevo registro.
     * @param dni DNI exacto a buscar.
     */
    public Optional<Cliente> findByDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    /**
     * Realiza una búsqueda flexible de clientes filtrando por apellidos.
     * Utilizado en la barra de búsqueda de la interfaz de usuario.
     * @param apellidos Texto a buscar (insensible a mayúsculas).
     */
    public List<Cliente> buscarPorApellidos(String apellidos) {
        return clienteRepository.findByApellidosContainingIgnoreCase(apellidos);
    }

    /**
     * Persiste (Crea o Actualiza) los datos de un cliente.
     * @param cliente Entidad con los datos a guardar.
     * @return El cliente persistido.
     */
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /**
     * Elimina un cliente del sistema.
     * @param id Identificador del cliente a eliminar.
     */
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }
}