package Transportation.Presentation.gui;

import Transportation.Service.DriverService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DriverMenuController {

    DriverService DriversHandler = new DriverService();

    @FXML
    private VBox removeDriverBox;

    @FXML
    private ComboBox<String> driverComboBox;

    @FXML
    private Button backButton;


    @FXML
    private void onViewAllDrivers() {
        String allDrivers = DriversHandler.viewAllDrivers();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("All Drivers");
        alert.setHeaderText("Drivers List");
        alert.setContentText(allDrivers);
        alert.showAndWait();
    }

    @FXML
    private void onAddDriver() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Driver");
        alert.setHeaderText("This will open Add Driver form");
        alert.setContentText("Add Driver not implemented yet.");
        alert.showAndWait();
    }

    @FXML
    private void onSetAvailability() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Availability");
        dialog.setHeaderText("Enter Driver ID");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            TextInputDialog avail = new TextInputDialog();
            avail.setTitle("Availability");
            avail.setHeaderText("Enter availability (true/false)");
            avail.showAndWait().ifPresent(status -> {
                try {
                    boolean available = Boolean.parseBoolean(status.trim());
                    DriversHandler.ChangeDriverAvailability(id, available);
                } catch (Exception e) {
                }
            });
        });
    }

    @FXML
    private void onAddLicense() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add License");
        dialog.setHeaderText("Enter Driver ID");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            TextInputDialog license = new TextInputDialog();
            license.setTitle("New License");
            license.setHeaderText("Enter License Type");
            license.showAndWait().ifPresent(lic -> {
                try {
                    DriversHandler.AddLicense(id, lic.trim());
                } catch (Exception e) {
                }
            });
        });
    }

    @FXML
    private void onShowRemoveDriver() {
        driverComboBox.getItems().clear();
        DriversHandler.viewAllDrivers();

        if (driverComboBox.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Drivers");
            alert.setHeaderText("No drivers to remove.");
            alert.showAndWait();
            return;
        }

        removeDriverBox.setVisible(true);
        removeDriverBox.setManaged(true);
    }

    @FXML
    private void onRemoveDriver() {

        String selectedDriver = driverComboBox.getValue();
        if (selectedDriver == null || selectedDriver.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No driver selected.");
            alert.showAndWait();
            return;
        }

        try {
            DriversHandler.deleteDriver(selectedDriver);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Removed");
            alert.setHeaderText("Driver removed: " + selectedDriver);
            alert.showAndWait();

            driverComboBox.getItems().remove(selectedDriver);
            driverComboBox.setValue(null);

            if (driverComboBox.getItems().isEmpty()) {
                removeDriverBox.setVisible(false);
                removeDriverBox.setManaged(false);
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not remove driver.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TransportationMenu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
