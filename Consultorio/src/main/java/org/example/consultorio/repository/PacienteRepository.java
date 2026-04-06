package org.example.consultorio.repository;

import org.example.consultorio.model.Paciente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteRepository {
    private static final String ARCHIVO = "pacientes.csv";

    public List<Paciente> cargarTodos() {
        List<Paciente> lista = new ArrayList<>();
        File archivo = new File(ARCHIVO);

        if (!archivo.exists()){
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))){
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()){
                    Paciente p = Paciente.formCSV(linea);
                    if (p != null){
                        lista.add(p);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return lista;
    }
    public void guardarTodos(List<Paciente> pacientes){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO, false))){
            for (Paciente p : pacientes){
                bw.write(p.toCSV());
                bw.newLine();
            }
        } catch (IOException e){
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }
}
