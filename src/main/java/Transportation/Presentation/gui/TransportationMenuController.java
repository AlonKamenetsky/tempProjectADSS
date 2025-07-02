package Transportation.Presentation.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TransportationMenuController {

    @FXML
    private void onTruckMenu() {
        openView("/fxml/TruckMenu.fxml");
    }

    @FXML
    private void onSiteMenu() {
        openView("/fxml/SiteMenu.fxml");
    }

    @FXML
    private void onZoneMenu() {
        openView("/fxml/ZoneMenu.fxml");
    }

    @FXML
    private void onTaskMenu() {
        openView("/fxml/TaskMenu.fxml");
    }

    @FXML
    private void onDriverMenu() {
        openView("/fxml/DriverMenu.fxml");
    }

    private void openView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) truckButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private javafx.scene.control.Button truckButton;

}
