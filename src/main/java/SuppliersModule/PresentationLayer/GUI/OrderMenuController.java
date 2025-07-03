package SuppliersModule.PresentationLayer.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class OrderMenuController {
    @FXML private Button backButton;

    public void onBack(ActionEvent actionEvent) throws IOException {
        openView("/fxml/Suppliers/SuppliersMenu.fxml", actionEvent);
    }

    public void onAddOrder(ActionEvent actionEvent) {
    }

    public void onAddScheduledOrder(ActionEvent actionEvent) {
    }

    public void onUpdateOrder(ActionEvent actionEvent) {
    }

    public void onDeleteOrder(ActionEvent actionEvent) {
    }

    public void onDeleteScheduledOrder(ActionEvent actionEvent) {
    }

    public void onPrintOrder(ActionEvent actionEvent) {
    }

    public void onPrintAllOrders(ActionEvent actionEvent) {
    }

    public void onPrintScheduledOrder(ActionEvent actionEvent) {
    }

    public void onPrintAllScheduledOrders(ActionEvent actionEvent) {
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
