package org.example.consultorio.model;

/**
 * Clase modelo que representa a un paciente del consultorio.
 *
 * <p>Aplica el principio de encapsulamiento de la Programación Orientada
 * a Obejetos: todos los atributos son privados y solo se acceden mediante metodos publicos (getters y setters).</p>
 *
 * <p>Tambien incluye metodos de serializacion/deserializavion CSV
 * para guardar y cargar pacientes desde un archivo de texto</p>
 *
 *@author Santiago Martínez y Axel Ernesto Equipo14
 *@version 1.0
 */
public class Paciente {

    // ==== ATRIBUTOS PRIVADOS ====
    // Al ser privados, nadie puede modificarlos directamente desde fuera
    //de la clase. Solo son accesibles a traves de los getters y setters

    /** Clace Unica de Registro de Poblacion. Identificador unico del paciente. */
    private String curp;
    /** Nombre completo del paciente (minimo 5 caracteres): */
    private String nombreCompleto;
    /** Edad del paciente en años. Debe estar entre 0 y 120. */
    private int edad;
    /** Numero de telefono de contacto. Solo digitos, minimo 10. */
    private String telefono;
    /** Alergias conocidas del paciente. Escribir "Ninguna" si no aplica. */
    private String alergias;
    /**
     * Estatus actual del paciente en el sistema.
     * Solo puede tener dos valores: "ACTIVO" o "INACTIVO".
     * Se usa para el borrado logico (no se borra fisicamente el registro).
     */
    private String estatus;

    // ==== CONSTRUCCIONES ====

    /**
     * Constructor vacio (sin parametros).
     *
     * <p>Necesario para crear un objeto Paciente "en blanco" y luego
     * asignarle datos uno por uno con los stters. Lo usa el metodo
     * formCSV() para reconstruir un paciente desde una linea del archivo.</p>
     */

    public Paciente() {}

    /**
     * Constructor completo con todos los parametros.
     *
     * <p>Permite crear un paciente con todos sus dato en una sola liena,
     * sin necesidad de llamar a 6 setters por separado.</p>
     *
     * @param curp          CURP unica del paciente
     * @param nombreCompleto Nombre completo (minimo 5 caracteres)
     * @param edad          Edad en años (0 a 120)
     * @param telefono      Telefono de contacto (solo digitos, min 10)
     * @param alergias      Alergias conocidas o "Ninguna"
     * @param estatus       Estado del paciente: "ACTIVO" o "INACTIVO"
     */

    public Paciente(String curp, String nombreCompleto, int edad, String telefono, String alergias, String estatus){
        this.curp = curp;
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.telefono = telefono;
        this.alergias = alergias;
        this.estatus = estatus;
    }

    // ==== GETTERS ====
    // Metodos publicos de solo lectura para cada atributo
    //JavaFX los usa internamente con PropertyValueaFactory para
    //mostrar los datos en las columnas de TableView

    /**
     * Obtiene la CURP del paciente.
     * @return CURP como String
     */
    public String getCurp() { return curp; }

    /**
     * Obtiene  el nombre completo del paciente.
     * @return Nombre completo como String
     */
    public String getNombreCompleto() { return nombreCompleto; }

    /**
     * Obtiene el nombre complero del paciente.
     * @return Nombre complero como String
     */
    public int getEdad() {return edad;}

    /**
     * Obtiene el telefono del paciente.
     * @return Edad como entero
     */
    public String getTelefono() {return telefono;}

    /**
     * Obtiene el telefono del paciente.
     * @return Telefono como String
     */
    public String getAlergias() {return alergias;}

    /**
     * Obtiene el estatus actual del paciente.
     * @return "ACTIVO" o "INACTIVO"
     */
    public String getEstatus() {return estatus;}

    // ==== SETTERS ====
    // Metodos publicos para modificar cada atributo.
    // Al ser metodos, en el futuro se podria agregar validacion aqui.

    /**
     * Estableve la CURP del paciente.
     * @param curp Nueva CURP
     */
    public void setCurp(String curp) {this.curp = curp;}

    /**
     * Establece el nombre completo del paciente.
     * @param nombreCompleto Nuevo nombre completo
     */
    public void setNombreCompleto(String nombreCompleto) {this.nombreCompleto = nombreCompleto;}

    /**
     * Establece la edad del paciente.
     * @param edad Nueva edad en años
     */
    public void setEdad(int edad) {this.edad = edad;}

    /**
     * Establece el telefono del paciente.
     * @param telefono Nuevo numero de telefono
     */
    public void setTelefono(String telefono) {this.telefono = telefono;}

    /**
     * Establece las alergias del paciente.
     * @param alergias Nuevas alervias o "Ninguna"
     */
    public void setAlergias(String alergias) {this.alergias = alergias;}

    /**
     * Establece el estatus del paciente
     * @param estatus "ACTIVO" o "INACTIVO"
     */
    public void setEstatus(String estatus) {this.estatus = estatus;}

    // ==== MÉTODOS DE SERIALIZACIÓN CSV ====

    /**
     * Convierte el paciente a una liena de contexto de formato CSV.
     *
     * <p>CSV (Comma Separated Values) es un formato de texto plano donde
     * cada campo esta separado por una coma. Es lo que se escribe en
     * el archivo pacientes.csv para guardar los datos.</p>
     *
     * <p>Ejepmlo de salida:
     * {@Code CURP001HDFRSS00,Maria Guadalupe Rosas, 34, 5512345678,Penicilina,ACTIVO}</p>
     *
     * @return String con los datos del pacinete separados por comas
     */
    public String toCSV(){
        // Concarenamos todos los campos separados por coma
        return curp + "," + nombreCompleto + "," + edad + "," + telefono + "," + alergias + "," + estatus;
    }

    /**
     * Crea un objeto Paciente a partir de una linea CSV leida del archivo.
     *
     * <p>Este metodo es el inverso de toCSV(). Lee una liea del archivo
     * y reconstruye el objeto Paciente con todos sus datos.</p>
     *
     * <p>El {@code split(",",6)} divide la linea por comas pero con
     * limite de 6 partes, para que si el campo "alergias" contiene
     * una coma (ej: "Polen, polvo"), no rompa el parseo.</p>
     *
     * <p>Es un metodo {@code static} poruqe no necesita una instancia
     * existente para funcionar: el crea instancias nuevas.</p>
     * @param linea Linea de texto en formato CSV leida del archivo
     * @return Objeto Paciente con los datos de la linea, o null si la liena es invalida
     */

    public static Paciente formCSV(String linea){
        // Dividimos la linea por coma, maximo 6 partes
        String[] partes = linea.split(",", 6);
        // Si la linea no tiene los 6 campos esperados, esta corrupata: ignorarla
        if (partes.length < 6) return null;
        // Creamos un paciente vacio y le asignamos cada campo
        Paciente p = new Paciente();
        p.setCurp(partes[0].trim());                      // trim() quita espacios
        p.setNombreCompleto(partes[1].trim());
        p.setEdad(Integer.parseInt(partes[2].trim()));    // Convertimos String a int
        p.setTelefono(partes[3].trim());
        p.setAlergias(partes[4].trim());
        p.setEstatus(partes[5].trim());
        return p;
    }

    /**
     * Representacion de texto del objeto Paciente.
     *
     * <p>Java llama a este metodo automaticamente cuando se imprime
     * un objeto o se concatena con un String. Muy util para depuracion
     * con {@code System.out.println(paciente)}.</p>
     * @return String con los datos ptincipales del paciente
     */
    @Override
    public String toString() {
        return "Paciente{curp ='" + curp + "', nombre='" + nombreCompleto + "', edad='" + edad + "', estatus'" + estatus + "'}";
    }
}
