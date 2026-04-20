module org.example.consultorio {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.consultorio to javafx.fxml;
    exports org.example.consultorio;

    opens org.example.consultorio.model to javafx.base;
}