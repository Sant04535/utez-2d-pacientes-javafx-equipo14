package org.example.consultorio;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.consultorio.model.Paciente;
import org.example.consultorio.service.PacienteService;

/**
 * capa del controlador es el mensajero del sistema.
 * recibe los mensajes del usuario (clics) y se las pasa al Service (oficina).
 */
public class HelloController {
    // componentes vinculados al archivo FXML mediante fx:id
    @FXML private TextField txtCurp, txtNombre, txtEdad, txtTelefono, txtAlergias;
    @FXML private Label lblActivos, lblInactivos;
    @FXML private TableView<Paciente> tablePacientes;
    @FXML private TableColumn<Paciente, String> colCurp, colNombre, colEstatus;

    // instancia del servicio para manejar la logica de negocio
    private PacienteService service;

    /**
     * initialize(): Se ejecuta automaticamente al cargar la vista.
     * Configura la tabla y carga los datos iniciales.
     */
    public void initialize() {
        try {
            service = new PacienteService();

            // PropertyValueFactory conecta las columnas con los atributos de Paciente.java
            colCurp.setCellValueFactory(new PropertyValueFactory<>("curp"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
            colEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));

            // Listener Cuando seleccionas una fila, los datos suben a los campos de texto para actualizar mas facil
            tablePacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    txtCurp.setText(newSelection.getCurp());
                    txtNombre.setText(newSelection.getNombreCompleto());
                    txtEdad.setText(String.valueOf(newSelection.getEdad()));
                    txtTelefono.setText(newSelection.getTelefono());
                    txtAlergias.setText(newSelection.getAlergias());
                }
            });

            actualizarVista();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * operacion create (CRUD) crea un objeto paciente y lo envia al service
     */
    @FXML
    protected void onBtnGuardarClick() {
        try {
            Paciente p = new Paciente(
                    txtCurp.getText(),
                    txtNombre.getText(),
                    Integer.parseInt(txtEdad.getText()),
                    txtTelefono.getText(),
                    txtAlergias.getText(),
                    "ACTIVO"
            );
            service.agregar(p); // envío a validaciones en el Service
            actualizarVista();
            limpiarCampos();
        } catch (Exception e) {
            // manejo de excepciones, muestra errores de validación al usuario
            alertar(e.getMessage());
        }
    }

    /**
     * Operación update (CRUD) Toma el paciente seleccionado y envia los cambios.
     */
    @FXML
    protected void onBtnActualizarClick() {
        Paciente seleccionado = tablePacientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                // creamos un nuevo objeto con los datos modificados del formulario
                Paciente editado = new Paciente(
                        txtCurp.getText(),
                        txtNombre.getText(),
                        Integer.parseInt(txtEdad.getText()),
                        txtTelefono.getText(),
                        txtAlergias.getText(),
                        seleccionado.getEstatus()
                );
                // le pasamos la CURP original para identificarlo y el nuevo objeto
                service.actualizar(seleccionado.getCurp(), editado);
                actualizarVista();
                limpiarCampos();
            } catch (Exception e) {
                alertar(e.getMessage());
            }
        } else {
            alertar("Selecciona un paciente de la tabla para editar.");
        }
    }

    /**
     * borrado Logico aambia el estado entre activo e inactivo
     */
    @FXML
    protected void onBtnEstatusClick() {
        Paciente seleccionado = tablePacientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                service.cambiarEstatus(seleccionado.getCurp());
                actualizarVista();
            } catch (Exception e) {
                alertar(e.getMessage());
            }
        }
    }

    /**
     * operación delate (CRUD) elimina físicamente el registro del sistema
     */
    @FXML
    protected void onBtnEliminarClick() {
        Paciente seleccionado = tablePacientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                service.eliminar(seleccionado.getCurp());
                actualizarVista();
            } catch (Exception e) {
                alertar(e.getMessage());
            }
        }
    }

    /**
     * refresca la tabla y los contadores de la interfaz
     */
    private void actualizarVista() {
        // setAll() notifica automáticamente a la tabla que hay cambios
        tablePacientes.getItems().setAll(service.obtenerTodos());
        lblActivos.setText("Activos: " + service.contarActivos());
        lblInactivos.setText("Inactivos: " + service.contarInactivos());
    }

    /**
     * limpia los campos de texto después de una operacion.
     */
    @FXML
    private void limpiarCampos() {
        txtCurp.clear();
        txtNombre.clear();
        txtEdad.clear();
        txtTelefono.clear();
        txtAlergias.clear();
    }

    /**
     * metodo auxiliar para mostrar mensajes informativos o de error
     */
    private void alertar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje);
        alert.show();
    }
}