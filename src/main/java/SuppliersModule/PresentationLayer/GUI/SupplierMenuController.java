package SuppliersModule.PresentationLayer.GUI;

import SuppliersModule.ServiceLayer.ServiceController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SupplierMenuController {
    ServiceController serviceController;
    public SupplierMenuController() {
        serviceController = ServiceController.getInstance();
    }
    public void onBack(ActionEvent actionEvent) throws IOException {
        openView("/fxml/Suppliers/SuppliersMenu.fxml", actionEvent);
    }

    public void onAddSupplier(ActionEvent actionEvent) {
    }

    public void onUpdateSupplier(ActionEvent actionEvent) {
    }

    public void onDeleteSupplier(ActionEvent actionEvent) {
    }


    public void onPrintAllSuppliers(ActionEvent actionEvent) {
    }

    public void onPrintSupplier(ActionEvent actionEvent) {
    }
    private void openView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
