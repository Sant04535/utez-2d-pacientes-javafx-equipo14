package org.example.consultorio;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.consultorio.model.Paciente;
import org.example.consultorio.service.PacienteService;

public class HelloController {
    @FXML private TextField txtCurp, txtNombre, txtEdad, txtTelefono, txtAlergias;
    @FXML private Label lblActivos, lblInactivos;
    @FXML private TableView<Paciente> tablePacientes;
    @FXML private TableColumn<Paciente, String> colCurp, colNombre, colEstatus;

    private PacienteService service;

    public void initialize() {
        try {
            service = new PacienteService();


            colCurp.setCellValueFactory(new PropertyValueFactory<>("curp"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
            colEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));


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
            service.agregar(p);
            actualizarVista();
            limpiarCampos();
        } catch (Exception e) {
            alertar(e.getMessage());
        }
    }

    @FXML
    protected void onBtnActualizarClick() {
        Paciente seleccionado = tablePacientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                Paciente editado = new Paciente(
                        txtCurp.getText(),
                        txtNombre.getText(),
                        Integer.parseInt(txtEdad.getText()),
                        txtTelefono.getText(),
                        txtAlergias.getText(),
                        seleccionado.getEstatus()
                );
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

    private void actualizarVista() {
        tablePacientes.getItems().setAll(service.obtenerTodos());
        lblActivos.setText("Activos: " + service.contarActivos());
        lblInactivos.setText("Inactivos: " + service.controlarInactivos());
    }

    @FXML
    private void limpiarCampos() {
        txtCurp.clear();
        txtNombre.clear();
        txtEdad.clear();
        txtTelefono.clear();
        txtAlergias.clear();
    }

    private void alertar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje);
        alert.show();
    }
}