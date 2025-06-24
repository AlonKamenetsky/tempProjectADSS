module org.example.tempprojectadss {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.tempprojectadss to javafx.fxml;
    exports org.example.tempprojectadss;
}