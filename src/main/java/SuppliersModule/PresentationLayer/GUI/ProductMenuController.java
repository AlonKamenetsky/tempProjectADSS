package SuppliersModule.PresentationLayer.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;

import java.io.IOException;

public class ProductMenuController {
    @FXML private Button addProductButton;
    @FXML private Button updateProductButton;
    @FXML private Button deleteProductButton;
    @FXML private Button printProductButton;
    @FXML private Button printAllProductsButton;
    @FXML private Button backButton;

    public void onBack(ActionEvent actionEvent) throws IOException {
        openView("/fxml/Suppliers/SuppliersMenu.fxml", actionEvent);
    }

    public void onPrintAllProducts(ActionEvent actionEvent) {
    }

    public void onPrintProduct(ActionEvent actionEvent) {
    }

    public void onUpdateProduct(ActionEvent actionEvent) {
    }

    public void onAddProduct(ActionEvent event) {

    }


    public void onDeleteProduct(ActionEvent actionEvent) {
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
