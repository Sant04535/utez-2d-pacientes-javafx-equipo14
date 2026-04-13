package org.example.consultorio.service;

import org.example.consultorio.model.Paciente;
import org.example.consultorio.repository.PacienteRepository;

import java.util.List;

/**
 * Servicio de logica de negocio para el manejo de pacientes.
 *
 * <p>Esta clase contiene todas las reglas y operaciones del sistema:
 * CURD completo, validaciones, deteccion de duplicados y contadores.</p>
 *
 * <p>Se ubica en la capa de SERVICIO de la arquitectura en 3 capas:
 * <ul>
 *     <li>El controller le pide operaciones al Service.</li>
 *     <li>El service valida y aplica las reglas del negocio.</li>
 *     <li>El Service le pide al Repository que persista los cambios.</li>
 * </ul>
 * </p>
 *
 * <p>Los datos se mantienen en memoria (lista) mientras la aplicacion
 * esta corriendo, y se sincronizan con el archivo en cada operacion
 * que modifica datos.</p>
 */
public class PacienteService {

    private final PacienteRepository repository;
    private List<Paciente> pacientes;

    public PacienteService() {
        this.repository = new PacienteRepository();
        this.pacientes = repository.cargarTodos();
    }
    public List<Paciente> obtenerTodos() {
        return pacientes;
    }
    public void agregar(Paciente nuevo) throws Exception{
        validar(nuevo);
        for (Paciente p: pacientes){
            if (p.getCurp().equalsIgnoreCase(nuevo.getCurp())){
                throw new Exception("Ya existe un paciente con ese Curp");
            }
        }
        pacientes.add(nuevo);
        repository.guardarTodos(pacientes);
    }
    public void actualizar(String curpOriginal, Paciente editado) throws Exception{
        validar(editado);
        for (Paciente p : pacientes){
            if (!p.getCurp().equalsIgnoreCase(curpOriginal) && p.getCurp().equalsIgnoreCase(editado.getCurp())){
                throw new Exception("Otro paciente ya tiene esa CURP.");
            }
        }
        repository.guardarTodos(pacientes);
    }
    public void cambiarEstatus(String curp) throws Exception{
        for (Paciente p : pacientes){
            if (p.getCurp().equalsIgnoreCase(curp)){
                if (p.getEstatus().equals("ACTIVO")){
                    p.setEstatus("INACTIVO");
                }else{
                    p.setEstatus("ACTIVO");
                }
                repository.guardarTodos(pacientes);
                return;
            }
        }
        throw new Exception("Paciente no encontrado.");
    }
    public void eliminar(String curp) throws Exception{
        boolean removido = pacientes.removeIf(p -> p.getCurp().equalsIgnoreCase(curp));
        if (!removido) throw new Exception("Paciente no encontrado");
        repository.guardarTodos(pacientes);
    }
    public int contarActivos(){
        return (int) pacientes.stream().filter(p -> "ACTIVO".equals(p.getEstatus())).count();
    }
    public int controlarInactivos(){
        return (int) pacientes.stream().filter(p -> "INACTIVO".equals(p.getEstatus())).count();
    }
    private void validar(Paciente p) throws Exception{
        if (p.getCurp() == null || p.getCurp().isBlank())throw new Exception("La CURP no puede estar vacia.");
        if (p.getNombreCompleto() == null || p.getNombreCompleto().isBlank())throw new Exception("El nombre no puede estar vacio");
        if (p.getEdad() < 0 || p.getEdad() > 120) throw new Exception("La edad debe estar entre 0 y 120.");
        if (p.getTelefono() == null || p.getTelefono().isBlank()) throw new Exception("El telefono no puede estar vacio.");
        if (!p.getTelefono().matches("\\d+")) throw new Exception("El telefono solo puedo contener digitos.");
        if (p.getTelefono().length() < 10) throw new Exception("El telefono debe tener al menos 10 digitos.");
        if (p.getAlergias() == null || p.getAlergias().isBlank()) throw new Exception("Las alegias no pueden estar vacias. Escribe 'Ninguna' si no aplica.");
    }
}
