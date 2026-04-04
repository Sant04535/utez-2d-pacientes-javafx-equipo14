package org.example.consultorio.model;

public class Paciente {
    private String curp;
    private String nombreCompleto;
    private int edad;
    private String telefono;
    private String alergias;
    private String estatus;

    public Paciente() {}

    public Paciente(String curp, String nombreCompleto, int edad, String telefono, String alergias, String estatus){
        this.curp = curp;
        this.nombreCompleto = nombreCompleto;
        this.edad = edad;
        this.telefono = telefono;
        this.alergias = alergias;
        this.estatus = estatus;
    }
    public String getCurp() { return curp; }
    public String getNombreCompleto() { return nombreCompleto; }
    public int getEdad() {return edad;}
    public String getTelefono() {return telefono;}
    public String getAlergias() {return alergias;}
    public String getEstatus() {return estatus;}

    public void setCurp(String curp) {this.curp = curp;}
    public void setNombreCompleto(String nombreCompleto) {this.nombreCompleto = nombreCompleto;}
    public void setEdad(int edad) {this.edad = edad;}
    public void setTelefono(String telefono) {this.telefono = telefono;}
    public void setAlergias(String alergias) {this.alergias = alergias;}
    public void setEstatus(String estatus) {this.estatus = estatus;}

    public String toCSV(){
        return curp + "," + nombreCompleto + "," + edad + "," + telefono + "," + alergias + "," + estatus;
    }

    public static Paciente formCSV(String linea){
        String[] partes = linea.split(",", 6);
        if (partes.length < 6) return null;
        Paciente p = new Paciente();
        p.setCurp(partes[0].trim());
        p.setNombreCompleto(partes[1].trim());
        p.setEdad(Integer.parseInt(partes[2].trim()));
        p.setTelefono(partes[3].trim());
        p.setAlergias(partes[4].trim());
        p.setEstatus(partes[5].trim());
        return p;

    }
    @Override
    public String toString() {
        return "Paciente{curp ='" + curp + "', nombre='" + nombreCompleto + "', edad='" + edad + "', estatus'" + estatus + "'}";
    }
}
