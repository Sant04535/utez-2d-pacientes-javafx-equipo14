package org.example.consultorio.service;

import org.example.consultorio.model.Paciente;
import org.example.consultorio.repository.PacienteRepository;

import java.util.List;

/**
 * Servicio de logica de negocio para el manejo de pacientes.
 *
 * <p>Esta clase contiene todas las reglas y operaciones del sistema:
 * CRUD completo, validaciones, deteccion de duplicados y contadores.</p>
 *
 * <p>Se ubica en la capa de SERVICIO de la arquitectura en 3 capas:
 * <ul>
 *   <li>El Controller le pide operaciones al Service.</li>
 *   <li>El Service valida y aplica las reglas del negocio.</li>
 *   <li>El Service le pide al Repository que persista los cambios.</li>
 * </ul>
 * </p>
 *
 * <p>Los datos se mantienen en memoria (lista) mientras la aplicacion
 * esta corriendo, y se sincronizan con el archivo en cada operacion
 * que modifica datos.</p>
 *
 * @author Equipo 14
 * @version 1.0
 */
public class PacienteService {

    /**
     * Repositorio para leer y guardar datos en el archivo CSV.
     * Usamos COMPOSICION: el Service "tiene un" Repository, no lo extiende.
     * Es final porque no necesitamos cambiar la referencia despues de crearlo.
     */
    private final PacienteRepository repository;

    /**
     * Lista de pacientes en memoria (RAM).
     * Se carga desde el archivo al iniciar y se mantiene actualizada
     * en cada operacion. Es mas rapido que leer el archivo en cada consulta.
     */
    private List<Paciente> pacientes;

    /**
     * Constructor del servicio.
     *
     * <p>Al crear el servicio, carga automaticamente los datos del archivo
     * mediante el repositorio. Asi, cuando la aplicacion inicia, ya tiene
     * todos los pacientes disponibles en memoria.</p>
     */
    public PacienteService() {
        this.repository = new PacienteRepository();
        // Cargamos los datos del archivo inmediatamente al iniciar
        this.pacientes = repository.cargarTodos();
    }

    /**
     * Obtiene la lista completa de todos los pacientes (activos e inactivos).
     *
     * <p>El Controller usa este metodo para poblar el TableView
     * con todos los registros disponibles.</p>
     *
     * @return Lista con todos los pacientes en memoria
     */
    public List<Paciente> obtenerTodos() {
        return pacientes;
    }

    /**
     * Agrega un nuevo paciente al sistema.
     *
     * <p>Proceso:
     * <ol>
     *   <li>Valida los datos del paciente (campos, formato, rangos).</li>
     *   <li>Verifica que no exista otro paciente con la misma CURP.</li>
     *   <li>Agrega el paciente a la lista en memoria.</li>
     *   <li>Persiste la lista completa en el archivo CSV.</li>
     * </ol>
     * </p>
     *
     * @param nuevo Paciente con los datos a registrar
     * @throws Exception Si los datos son invalidos o la CURP ya existe
     */
    public void agregar(Paciente nuevo) throws Exception {
        // Primero validamos que los datos sean correctos
        validar(nuevo);

        // Verificamos que no haya duplicado por CURP (identificador unico)
        for (Paciente p : pacientes) {
            if (p.getCurp().equalsIgnoreCase(nuevo.getCurp())) {
                throw new Exception("Ya existe un paciente con esa CURP.");
            }
        }

        // Si paso todas las validaciones, lo agregamos
        pacientes.add(nuevo);

        // Guardamos la lista actualizada en el archivo
        repository.guardarTodos(pacientes);
    }

    /**
     * Actualiza los datos de un paciente existente.
     *
     * <p>Se recibe la CURP original (antes de editar) porque el usuario
     * podria haber cambiado la CURP en el formulario. Necesitamos la
     * CURP original para encontrar el registro correcto en la lista.</p>
     *
     * <p>La validacion de duplicado comprueba que la nueva CURP no exista
     * en OTRO paciente diferente al que se esta editando.</p>
     *
     * @param curpOriginal CURP del paciente antes de la edicion
     * @param editado      Paciente con los nuevos datos
     * @throws Exception Si los datos son invalidos o la nueva CURP pertenece a otro paciente
     */
    public void actualizar(String curpOriginal, Paciente editado) throws Exception {
        // Validamos los nuevos datos
        validar(editado);

        // Verificamos que la nueva CURP no este en uso por OTRO paciente
        for (Paciente p : pacientes) {
            // Ignoramos al mismo paciente (su CURP original), solo revisamos los demas
            if (!p.getCurp().equalsIgnoreCase(curpOriginal)
                    && p.getCurp().equalsIgnoreCase(editado.getCurp())) {
                throw new Exception("Otro paciente ya tiene esa CURP.");
            }
        }

        // Buscamos la posicion del paciente en la lista y lo reemplazamos
        for (int i = 0; i < pacientes.size(); i++) {
            if (pacientes.get(i).getCurp().equalsIgnoreCase(curpOriginal)) {
                pacientes.set(i, editado); // Reemplazamos en la misma posicion
                break;
            }
        }

        // Guardamos los cambios en el archivo
        repository.guardarTodos(pacientes);
    }

    /**
     * Cambia el estatus del paciente entre ACTIVO e INACTIVO (borrado logico).
     *
     * <p>En lugar de eliminar fisicamente el registro, simplemente se cambia
     * el campo estatus. Esto se conoce como "borrado logico" y es la practica
     * usada en sistemas reales de salud para mantener historial completo.</p>
     *
     * <p>Si el paciente estaba ACTIVO pasa a INACTIVO, y viceversa.</p>
     *
     * @param curp CURP del paciente al que se le cambiara el estatus
     * @throws Exception Si no se encuentra ningun paciente con esa CURP
     */
    public void cambiarEstatus(String curp) throws Exception {
        for (Paciente p : pacientes) {
            if (p.getCurp().equalsIgnoreCase(curp)) {
                // Toggle: si es ACTIVO lo ponemos INACTIVO y viceversa
                if (p.getEstatus().equals("ACTIVO")) {
                    p.setEstatus("INACTIVO");
                } else {
                    p.setEstatus("ACTIVO");
                }
                // Persistimos el cambio inmediatamente
                repository.guardarTodos(pacientes);
                return; // Terminamos al encontrarlo
            }
        }
        // Si llegamos aqui, no encontramos el paciente
        throw new Exception("Paciente no encontrado.");
    }

    /**
     * Elimina fisicamente un paciente del sistema.
     *
     * <p>A diferencia de cambiarEstatus(), este metodo borra el registro
     * de forma permanente. El Controller muestra un dialogo de confirmacion
     * antes de llamar a este metodo.</p>
     *
     * <p>Usa {@code removeIf()} con una expresion lambda para buscar
     * y eliminar el paciente en una sola operacion. Devuelve {@code true}
     * si elimino algo, {@code false} si no encontro el registro.</p>
     *
     * @param curp CURP del paciente a eliminar
     * @throws Exception Si no se encuentra ningun paciente con esa CURP
     */
    public void eliminar(String curp) throws Exception {
        // removeIf elimina todos los elementos que cumplan la condicion lambda
        // equalsIgnoreCase para no distinguir entre mayusculas y minusculas
        boolean removido = pacientes.removeIf(
                p -> p.getCurp().equalsIgnoreCase(curp)
        );

        // Si no elimino nada, el paciente no existia
        if (!removido) throw new Exception("Paciente no encontrado.");

        // Guardamos la lista sin el paciente eliminado
        repository.guardarTodos(pacientes);
    }

    /**
     * Cuenta el numero de pacientes con estatus ACTIVO.
     *
     * <p>Usa Streams de Java (programacion funcional):
     * <ul>
     *   <li>{@code stream()} convierte la lista en un flujo de datos.</li>
     *   <li>{@code filter()} filtra solo los que cumplen la condicion.</li>
     *   <li>{@code count()} cuenta cuantos quedaron.</li>
     * </ul>
     * </p>
     *
     * <p>El resultado se muestra en el Label "Activos" del area de resumen
     * en la pantalla principal.</p>
     *
     * @return Numero de pacientes activos
     */
    public int contarActivos() {
        return (int) pacientes.stream()
                .filter(p -> "ACTIVO".equals(p.getEstatus()))
                .count();
    }

    /**
     * Cuenta el numero de pacientes con estatus INACTIVO.
     *
     * <p>Funciona igual que {@link #contarActivos()} pero filtra
     * por "INACTIVO". El resultado se muestra en el Label "Inactivos"
     * del area de resumen.</p>
     *
     * @return Numero de pacientes inactivos
     */
    public int contarInactivos() {
        return (int) pacientes.stream()
                .filter(p -> "INACTIVO".equals(p.getEstatus()))
                .count();
    }

    /**
     * Valida que los datos de un paciente sean correctos antes de guardarlos.
     *
     * <p>Este metodo es PRIVADO porque es un detalle interno del servicio.
     * Se llama desde agregar() y actualizar() antes de hacer cualquier
     * operacion. Si algo no cumple las reglas, lanza una Exception
     * con un mensaje descriptivo que el Controller mostrara al usuario.</p>
     *
     * <p>Reglas validadas:
     * <ul>
     *   <li>CURP: no puede estar vacia.</li>
     *   <li>Nombre: no vacio, minimo 5 caracteres.</li>
     *   <li>Edad: entre 0 y 120 años.</li>
     *   <li>Telefono: no vacio, solo digitos, minimo 10 caracteres.</li>
     *   <li>Alergias: no vacio (escribir "Ninguna" si no aplica).</li>
     * </ul>
     * </p>
     *
     * @param p Paciente cuyos datos se van a validar
     * @throws Exception Con mensaje descriptivo si alguna validacion falla
     */
    private void validar(Paciente p) throws Exception {
        // Validacion de CURP: no puede ser nula ni solo espacios
        if (p.getCurp() == null || p.getCurp().isBlank())
            throw new Exception("La CURP no puede estar vacia.");

        // Validacion de nombre: no vacio y minimo 5 caracteres
        if (p.getNombreCompleto() == null || p.getNombreCompleto().isBlank())
            throw new Exception("El nombre no puede estar vacio.");
        if (p.getNombreCompleto().trim().length() < 5)
            throw new Exception("El nombre debe tener al menos 5 caracteres.");

        // Validacion de edad: rango logico de 0 a 120 años
        if (p.getEdad() < 0 || p.getEdad() > 120)
            throw new Exception("La edad debe estar entre 0 y 120.");

        // Validacion de telefono: no vacio, solo digitos, minimo 10
        if (p.getTelefono() == null || p.getTelefono().isBlank())
            throw new Exception("El telefono no puede estar vacio.");

        // matches("\\d+") = expresion regular que verifica solo digitos (0-9)
        // \\d = cualquier digito, + = uno o mas
        if (!p.getTelefono().matches("\\d+"))
            throw new Exception("El telefono solo puede contener digitos.");
        if (p.getTelefono().length() < 10)
            throw new Exception("El telefono debe tener al menos 10 digitos.");

        // Validacion de alergias: no puede estar vacio
        if (p.getAlergias() == null || p.getAlergias().isBlank())
            throw new Exception("Las alergias no pueden estar vacias. Escribe 'Ninguna' si no aplica.");
    }
}