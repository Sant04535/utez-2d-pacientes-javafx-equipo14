package org.example.consultorio.repository;

import org.example.consultorio.model.Paciente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de pacientes. Maneja la persistencia de datos en archivo CSV.
 *
 * <p>Esta clase es la UNICA responsable de leer y escribir en el archivo
 * de datos {@code pacientes.csv}. Ninguna otra clase del sistema toca
 * el archivo directamente.</p>
 *
 * <p>Aplica el principio de Responsabilidad Unica (SRP): su unico trabajo
 * es la lectura y escritura de datos. Si en el futuro se quisiera cambiar
 * de archivo CSV a base de datos, solo se modifica esta clase.</p>
 *
 * <p>El archivo se ubica en el directorio raiz del proyecto (donde esta oin.xml).
 * Al ejecutar desde Intellij, ese es el directorio de trabajo por defecto.</p>
 *
 * @author Santiago Martíez y Axel Ernesto Equipo14
 * @version 1.0
 */
public class PacienteRepository {
    /**
     * Nombre del archivo donde se guardan los datos de los pacientes.
     * Es una constante (static final) para que sea facil de cambiar
     * en un solo lugar si se necesita renombrar el archivo.
     */
    private static final String ARCHIVO = "pacientes.csv";

    /**
     * Carga todos los pacientes desde el archivo CSV.
     *
     * <p>Lee el archivo linea por linea y convierte cada liena en un
     * objeto Paciente usando {@link Paciente#fromCSV(String)}</p>
     *
     * <p>Usa try-with-resources para cerrar el BufferedReader
     * automaticamente al terminar, aunque ocurra una excepcion.
     * Esto evita fugas de recursos (resource leaks).</p>
     * @return Lista de pacientes cargados desde el archivo.
     *         Lista vacia si el archivo no existe a esta vacio.
     */

    public List<Paciente> cargarTodos() {
        List<Paciente> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);

        // Si el archivo no existe aun, regresamos lista vacia (primera ejecución)
        if (!archivo.exists()){
            return lista;
        }

        // try-with-resources: el BufferedReader se cierra automaticamente
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))){
            String linea;

            // readLine() devuelve bull cuando ya no hay mas lineas
            while ((linea = br.readLine()) != null) {

                // Ignoramos lineas vacias o con solo espacios
                if (!linea.trim().isEmpty()){
                    Paciente p = Paciente.formCSV(linea);

                    // Solo agregamos si el parseo fue exitoso (no null)
                    if (p != null){
                        lista.add(p);
                    }
                }
            }
        } catch (IOException e) {
            // Mostramos el error en consola pero no detenemos el programa
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Guarda todos los pacientes en el archivo CSV, sobreescribiendolo.
     *
     * <p>Este metodo sobreescribe el archivo completo cada vez que se llama.
     * El parametro {@code false} en {@code FileWriter(ARCHIVO, false)} indica
     * que NO se usa modo "append" (agregar al final), sino que se reemplaza
     * todo el contenido. Esto garantiza que no queden registros duplicados
     * ni registros "fantasma" de pacientes eliminados.</p>
     *
     * <p>Usa BufferedWriter para escribir de forma mas eficiente:
     * acumula datos de memoria y los escribe al disco en bloques,
     * en lugar de hacer una operacion de disco por cada liena.</p>
     *
     * @param pacientes Lista completa de pacientes a guardar en el archivo
     */
    public void guardarTodos(List<Paciente> pacientes){
        // false = sobreescribir el archivo (no agregar al final)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, false))){
            for (Paciente p : pacientes){
                bw.write(p.toCSV()); // Escribe la liena CSV del paciente
                bw.newLine();        // Agrega salto de linea despues de cada registro
            }
        } catch (IOException e){
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }
}
