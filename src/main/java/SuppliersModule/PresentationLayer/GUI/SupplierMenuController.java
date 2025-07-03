package SuppliersModule.PresentationLayer.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SupplierMenuController {
    @FXML private Button addSupplierButton;
    @FXML private Button updateSupplierButton;
    @FXML private Button deleteSupplierButton;
    @FXML private Button printSupplierButton;
    @FXML private Button printAllSuppliersButton;
    @FXML private Button backButton;
    @FXML
    private void onBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Suppliers/SupplierMenu.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
