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

public class ContractMenuController {
    ServiceController serviceController;
    public ContractMenuController() {
        this.serviceController = ServiceController.getInstance();
    }
    @FXML
    private void onBack(ActionEvent actionEvent) throws IOException {
        openView("/fxml/Suppliers/SuppliersMenu.fxml", actionEvent);
    }

    public void onAddContract(ActionEvent actionEvent) {
    }

    public void onDeleteContract(ActionEvent actionEvent) {
    }

    public void onPrintContract(ActionEvent actionEvent) {
    }

    public void onPrintAllContracts(ActionEvent actionEvent) {
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
