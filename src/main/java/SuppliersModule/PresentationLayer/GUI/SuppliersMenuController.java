package SuppliersModule.PresentationLayer.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SuppliersMenuController {

    public void onProductMenu(ActionEvent actionEvent) {
        openView("/fxml/Suppliers/ProductMenu.fxml", actionEvent);
    }

    public void onSupplierMenu(ActionEvent actionEvent) {
        openView("/fxml/Suppliers/SupplierMenu.fxml", actionEvent);
    }

    public void onContractMenu(ActionEvent actionEvent) {
        openView("/fxml/Suppliers/ContractMenu.fxml", actionEvent);
    }

    public void onOrderMenu(ActionEvent actionEvent) {
        openView("/fxml/Suppliers/OrderMenu.fxml", actionEvent);
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

    public void onSwitchToTransportation(ActionEvent actionEvent) {
        openView("/fxml/TransportationMenu.fxml", actionEvent);
    }
}
